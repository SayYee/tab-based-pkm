package com.sayyi.software.tbp.generator.dsl.parser.impl;

import com.sayyi.software.tbp.generator.dsl.parser.Parser;

import java.nio.CharBuffer;

public class EnterParser implements Parser<Boolean> {

    @Override
    public Boolean parse(CharBuffer charBuffer) {
        int start = charBuffer.position();
        while (charBuffer.hasRemaining()) {
            char c = charBuffer.get();
            if (!isEnter(c)) {
                break;
            }
        }
        int end = charBuffer.position() - 1;
        charBuffer.position(end);
        return end - start == 0;
    }

    @Override
    public boolean match(CharBuffer charBuffer) {
        if (!charBuffer.hasRemaining()) {
            return false;
        }
        charBuffer.mark();
        boolean result = isEnter(charBuffer.get());
        charBuffer.reset();
        return result;
    }
}
