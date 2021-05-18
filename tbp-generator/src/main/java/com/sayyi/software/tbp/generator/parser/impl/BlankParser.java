package com.sayyi.software.tbp.generator.parser.impl;

import com.sayyi.software.tbp.generator.parser.Parser;

import java.nio.CharBuffer;

public class BlankParser implements Parser<Integer> {
    @Override
    public Integer parse(CharBuffer charBuffer) {
        int start = charBuffer.position();
        while (charBuffer.hasRemaining()) {
            char c = charBuffer.get();
            if (!isBlank(c)) {
                break;
            }
        }
        int end = charBuffer.position() - 1;
        charBuffer.position(end);
        return end - start;
    }

    @Override
    public boolean match(CharBuffer charBuffer) {
        if (!charBuffer.hasRemaining()) {
            return false;
        }
        charBuffer.mark();
        boolean result = isBlank(charBuffer.get());
        charBuffer.reset();
        return result;
    }
}
