package com.sayyi.software.tbp.generator.parser.impl;

import com.sayyi.software.tbp.generator.parser.Parser;

import java.nio.CharBuffer;

public class EnterParser implements Parser<Boolean> {

    private static final char[] ENTER = System.getProperty("line.separator").toCharArray();

    @Override
    public Boolean parse(CharBuffer charBuffer) {
        return matchAndJump(charBuffer, ENTER);
    }

    @Override
    public boolean match(CharBuffer charBuffer) {
        return match(charBuffer, ENTER);
    }
}
