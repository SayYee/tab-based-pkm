package com.sayyi.software.tbp.generator.parser.impl;

import cn.hutool.core.lang.Assert;
import com.sayyi.software.tbp.generator.model.FieldInfo;
import com.sayyi.software.tbp.generator.parser.Parser;
import com.sayyi.software.tbp.generator.parser.ParserBuilder;

import java.nio.CharBuffer;

public class FieldParser implements Parser<FieldInfo> {

    private final StringParser stringParser;
    private final BlankParser blankParser;

    public FieldParser(StringParser stringParser, BlankParser blankParser) {
        this.stringParser = stringParser;
        this.blankParser = blankParser;
    }

    @Override
    public FieldInfo parse(CharBuffer charBuffer) {
        FieldInfo fieldInfo = new FieldInfo();
        ParserBuilder parserBuilder = new ParserBuilder();

        // 1、读取字符串
        // 2、读取空格，数量不能为0
        // 3、读取字符串
        // 4、读取空格，数量随意；读取结束标志
        parserBuilder.add(stringParser, fieldInfo::setFieldType)
                .next()
                .add(blankParser)
                .next()
                .add(stringParser, fieldInfo::setFieldName)
                .next()
                .add(blankParser)
                .add(CharBuffer::get, c -> Assert.isTrue(c == ';', "必须以；结尾"));
        parserBuilder.execute(charBuffer);
        return fieldInfo;
    }

    @Override
    public boolean match(CharBuffer charBuffer) {
        return stringParser.match(charBuffer);
    }

}
