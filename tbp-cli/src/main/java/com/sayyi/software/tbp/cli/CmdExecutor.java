package com.sayyi.software.tbp.cli;

import com.sayyi.software.tbp.cli.decorator.CmdPkmFunction;
import com.sayyi.software.tbp.cli.decorator.ResultHolder;
import com.sayyi.software.tbp.core.facade.PkmFunction;
import picocli.CommandLine;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 命令执行者
 * @author SayYi
 */
public class CmdExecutor {

    private final PkmFunction pkmFunction;
    private final PkmFunction wrapper;
    private final ResultHolder resultHolder;
    private final CommandLine commandLine;

    public CmdExecutor(PkmFunction pkmFunction) {
        this.pkmFunction = pkmFunction;
        resultHolder = new ResultHolder();
        wrapper = CmdPkmFunction.create(pkmFunction, resultHolder);
        PkmCommand pkmCommand = new PkmCommand(wrapper);
        commandLine = new CommandLine(pkmCommand)
                .setExecutionExceptionHandler((ex, commandLine1, parseResult) -> {
                    resultHolder.setException(ex);
                    return 0;
                }).setParameterExceptionHandler((ex, args) -> {
                    resultHolder.setException(ex);
                    return 0;
                });
    }

    /***
     * 调用命令并返回执行结果
     * @param args  参数
     * @return  处理结果。可能为空
     * @throws Exception    调用过程中发生的异常信息
     */
    public synchronized Object execute(String... args) throws Exception {
        commandLine.execute(args);
        return resultHolder.getResult();
    }

    /**
     * 获取命令的使用信息
     * @return  key-命令名称 value-命令描述
     */
    public synchronized Map<String, String> commandUsageInfo() {
        return commandLine.getCommandSpec().subcommands().entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getUsageMessage()));
    }
}
