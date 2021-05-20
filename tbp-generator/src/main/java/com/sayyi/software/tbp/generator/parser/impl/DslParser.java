package com.sayyi.software.tbp.generator.parser.impl;

import cn.hutool.core.lang.Assert;
import com.sayyi.software.tbp.generator.model.ModuleInfo;
import com.sayyi.software.tbp.generator.parser.Parser;
import com.sayyi.software.tbp.generator.parser.ParserBuilder;

import java.nio.CharBuffer;
import java.util.LinkedList;
import java.util.List;

public class DslParser implements Parser<List<ModuleInfo>> {

    private final ModuleParser moduleParser;
    private final NoteParser noteParser;
    private final BlankParser blankParser;
    private final EnterParser enterParser;

    public DslParser(ModuleParser moduleParser,
                     NoteParser noteParser,
                     BlankParser blankParser,
                     EnterParser enterParser) {
        this.moduleParser = moduleParser;
        this.noteParser = noteParser;
        this.blankParser = blankParser;
        this.enterParser = enterParser;
    }

    @Override
    public List<ModuleInfo> parse(CharBuffer charBuffer) {
        List<ModuleInfo> moduleInfos = new LinkedList<>();
        ParserBuilder parserBuilder = new ParserBuilder();
        // 最后一个需要注意的问题是，如果buffer空了，此时尝试执行match会报错
        // 可以设置 match ，在数据为空时，不匹配属性
        parserBuilder.add(blankParser)
                .add(enterParser)
                .add(noteParser)
                .add(moduleParser, moduleInfos::add)
                .add(CharBuffer::hasRemaining, t -> Assert.isTrue(!t, "还有别的无法识别的内容"));
        parserBuilder.execute(charBuffer);
        return moduleInfos;
    }

    @Override
    public boolean match(CharBuffer charBuffer) {
        return true;
    }
}
