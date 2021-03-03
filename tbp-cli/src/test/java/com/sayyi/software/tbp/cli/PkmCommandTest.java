package com.sayyi.software.tbp.cli;

import com.sayyi.software.tbp.core.facade.PkmFunction;
import org.junit.Test;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

/**
 * @author SayYi
 */
public class PkmCommandTest {

    private final PkmFunction pkmFunction = mock(PkmFunction.class);

    private final CommandLine commandLine = new CommandLine(new PkmCommand(pkmFunction));

    @Test
    public void test_delete_tag() throws Exception {
        String[] args = {"deleteTag", "temp"};
        commandLine.execute(args);
        verify(pkmFunction).deleteTag("temp");
    }

    @Test
    public void test_rename_tag() throws Exception {
        String[] args = {"renameTag", "temp", "new_temp"};
        commandLine.execute(args);
        verify(pkmFunction).renameTag("temp", "new_temp");
    }

    @Test
    public void test_rename_tag_with_blank() throws Exception {
        String cmd = "renameTag  temp  new_temp";
        String[] split = cmd.split(" ");
        split = Arrays.stream(split).filter(s -> s.length() != 0).toArray(String[]::new);
        commandLine.execute(split);
        verify(pkmFunction).renameTag("temp", "new_temp");
    }

    @Test
    public void test_batch_modify_tags() throws Exception {
        String[] args = {"batchModifyTags", "temp.now", "temp.now.add"};
        commandLine.execute(args);
        Set<String> tags = new HashSet<>();
        tags.add("temp");
        tags.add("now");
        Set<String> newTags = new HashSet<>();
        newTags.add("temp");
        newTags.add("now");
        newTags.add("add");
        verify(pkmFunction).batchModifyTags(tags, newTags);
    }

    @Test
    public void test_delete_tag_error() throws Exception {
        String[] args = {"deleteTag"};
        commandLine.execute(args);
        verify(pkmFunction, never()).deleteTag(null);
    }

    @Test
    public void test_cmd_description() throws Exception {
        commandLine.getCommandSpec().subcommands().values()
                .forEach(cmd -> System.out.print(cmd.getUsageMessage()));
    }

}
