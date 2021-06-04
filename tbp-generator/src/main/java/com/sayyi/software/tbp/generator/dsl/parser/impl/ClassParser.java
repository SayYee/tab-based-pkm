package com.sayyi.software.tbp.generator.dsl.parser.impl;

import cn.hutool.core.lang.Assert;
import com.sayyi.software.tbp.generator.dsl.model.ClassInfo;
import com.sayyi.software.tbp.generator.dsl.model.FieldInfo;
import com.sayyi.software.tbp.generator.dsl.parser.Parser;
import com.sayyi.software.tbp.generator.dsl.parser.ParserBuilder;

import java.nio.CharBuffer;
import java.util.*;

public class ClassParser implements Parser<ClassInfo> {

    private static final char[] CLASS = "class".toCharArray();

    private final StringParser stringParser;
    private final FieldParser fieldParser;
    private final NoteParser noteParser;
    private final BlankParser blankParser;
    private final EnterParser enterParser;

    public ClassParser(StringParser stringParser,
                       FieldParser fieldParser,
                       NoteParser noteParser,
                       BlankParser blankParser,
                       EnterParser enterParser) {
        this.stringParser = stringParser;
        this.fieldParser = fieldParser;
        this.noteParser = noteParser;
        this.blankParser = blankParser;
        this.enterParser = enterParser;
    }

    @Override
    public ClassInfo parse(CharBuffer charBuffer) {
        ClassInfo classInfo = new ClassInfo();
        List<FieldInfo> fieldInfos = new LinkedList<>();
        classInfo.setFields(fieldInfos);
        // 1、class开头
        // 2、必须跟空白
        // 3、类名
        // 4、空白、换行、花括号
        // 5、空白、换行、注释、field、花括号
        ParserBuilder parserBuilder = new ParserBuilder();
        parserBuilder.add(stringParser, sign -> Assert.isTrue("class".equals(sign), "必须以class开头"))
                .next()
                .add(blankParser, i -> Assert.isTrue(i > 0, "class标志后边必须有空白元素"))
                .next()
                .add(stringParser, classInfo::setClassName)
                .next()
                .add(blankParser)
                .add(enterParser)
                .add(CharBuffer::get, c -> Assert.isTrue(c == '{', "class块必须是花括号开启"))
                .next()
                .add(blankParser)
                .add(enterParser)
                .add(noteParser)
                .add(fieldParser, fieldInfos::add)
                .add(CharBuffer::get, c -> Assert.isTrue(c == '}', "class块必须是花括号结束"));
        parserBuilder.execute(charBuffer);
        return classInfo;
    }

    @Override
    public boolean match(CharBuffer charBuffer) {
        return match(charBuffer, 'c');
    }
}
