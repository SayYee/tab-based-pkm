package com.sayyi.software.tbp.cli;

import com.sayyi.software.tbp.cli.util.RequestSender;
import com.sayyi.software.tbp.common.TbpConfig;
import com.sayyi.software.tbp.common.TbpConfigParse;
import com.sayyi.software.tbp.nio.client.TbpClient;
import org.fusesource.jansi.AnsiConsole;
import org.jline.console.impl.SystemRegistryImpl;
import org.jline.keymap.KeyMap;
import org.jline.reader.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.widget.TailTipWidgets;
import picocli.CommandLine;
import picocli.CommandLine.*;
import picocli.shell.jline3.PicocliCommands;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author SayYi
 */
@Command(name = "", description = "PKM系统命令行", mixinStandardHelpOptions = true, subcommands = {
        HelpCommand.class,
        FileOpCommand.class,
        LsCommand.class,
        UtilComand.class
})
public class CliCommand {
    LineReaderImpl reader;
    PrintWriter out;

    RequestSender sender;

    public void setReader(LineReader reader) {
        this.reader = (LineReaderImpl) reader;
        out = reader.getTerminal().writer();
    }

    public void setRequestSender(RequestSender requestSender) {
        this.sender = requestSender;
    }

    private static Path workDir() {
        return Paths.get(System.getProperty("user.dir"));
    }

    public static void main(String[] args) throws IOException {
        TbpConfig tbpConfig = new TbpConfigParse(args[0]);

        AnsiConsole.systemInstall();
        TbpClient tbpClient = new TbpClient(tbpConfig.getPort());
        tbpClient.start();
        try {
            CliCommand cliCommand = new CliCommand();
            CommandLine cmd = new CommandLine(cliCommand);
            PicocliCommands picocliCommands = new PicocliCommands(CliCommand::workDir, cmd);

            DefaultParser parser = new DefaultParser();
            try (Terminal terminal = TerminalBuilder.terminal()) {
                SystemRegistryImpl systemRegistry = new SystemRegistryImpl(parser, terminal, CliCommand::workDir, null);
                systemRegistry.setCommandRegistries(picocliCommands);

                LineReader reader = LineReaderBuilder.builder()
                        .terminal(terminal)
                        .completer(systemRegistry.completer())
                        .parser(parser)
                        .variable(LineReader.LIST_MAX, 50)
                        .build();

                cliCommand.setReader(reader);
                RequestSender requestSender = new RequestSender(tbpClient);
                cliCommand.setRequestSender(requestSender);

                TailTipWidgets widgets = new TailTipWidgets(reader, systemRegistry::commandDescription, 5, TailTipWidgets.TipType.COMPLETER);
                widgets.enable();
                KeyMap<Binding> keyMap = reader.getKeyMaps().get("main");
                keyMap.bind(new Reference("tailtip-toggle"), KeyMap.alt("s"));

                String prompt = "PKM> ";
                String line;
                while (true) {
                    try {
                        systemRegistry.cleanUp();
                        line = reader.readLine(prompt);
                        systemRegistry.execute(line);
                    } catch (UserInterruptException e) {
                        // Ignore
                    } catch (EndOfFileException e) {
                        return;
                    } catch (Exception e) {
                        systemRegistry.trace(e);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            tbpClient.shutdown();
            AnsiConsole.systemUninstall();
        }
    }
}
