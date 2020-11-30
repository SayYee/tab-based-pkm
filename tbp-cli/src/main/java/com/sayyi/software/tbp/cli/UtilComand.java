package com.sayyi.software.tbp.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

/**
 * @author SayYi
 */
@Command(name = "cls", aliases = "clear", mixinStandardHelpOptions = true,
        description = "Clears the screen", version = "1.0")
public class UtilComand implements Runnable {
    @ParentCommand
    CliCommand cmd;

    @Override
    public void run() {
        cmd.reader.clearScreen();
    }
}
