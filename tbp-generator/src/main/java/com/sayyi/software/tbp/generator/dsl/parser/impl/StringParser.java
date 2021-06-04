package com.sayyi.software.tbp.generator.dsl.parser.impl;

import com.sayyi.software.tbp.generator.dsl.parser.Parser;

import java.nio.CharBuffer;

public class StringParser implements Parser<String> {

    @Override
    public String parse(CharBuffer charBuffer) {
        int start = charBuffer.position();
        if (!match(charBuffer)) {
            throw new IllegalArgumentException("匹配失败");
        }
        // 使用 空白字符 作为两个field之间的分割符
        // 遍历的数据只能是 合法字符。如果是不合法的数字，就跳出循环。
        while (charBuffer.hasRemaining()) {
            char c = charBuffer.get();
            if (!isValid(c)) {
                break;
            }
        }
        if (!charBuffer.hasRemaining()) {
            throw new IllegalArgumentException("异常");
        }
        // 结束位置，不包含非法字符
        int endPosition = charBuffer.position() - 1;
        charBuffer.position(start);
        char[] chars = new char[endPosition - start];
        charBuffer.get(chars);
        return String.valueOf(chars);
    }

    @Override
    public boolean match(CharBuffer charBuffer) {
        if (!charBuffer.hasRemaining()) {
            return false;
        }
        charBuffer.mark();
        boolean isLetter = isLetter(charBuffer.get());
        charBuffer.reset();
        return isLetter;
    }
}
