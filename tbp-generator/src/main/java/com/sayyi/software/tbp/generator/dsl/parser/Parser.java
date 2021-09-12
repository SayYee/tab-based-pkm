package com.sayyi.software.tbp.generator.dsl.parser;

import java.nio.CharBuffer;
import java.util.Arrays;

@FunctionalInterface
public interface Parser<T> {

    /**
     * 解析，并获取结果。如果数据不匹配或者数据格式错误，会抛出异常
     * @param charBuffer
     * @return
     */
    T parse(CharBuffer charBuffer);

    /**
     * 判断匹配当前解析器
     * @param charBuffer
     * @return
     */
    default boolean match(CharBuffer charBuffer) {
        return true;
    }

    /**
     * charBuffer接下来的字符是否匹配target。不会变更指针位置
     * @param charBuffer
     * @param target
     * @return
     */
    default boolean match(CharBuffer charBuffer, char... target) {
        if (!charBuffer.hasRemaining()) {
            return false;
        }
        char[] array = new char[target.length];
        charBuffer.mark();
        charBuffer.get(array);
        charBuffer.reset();
        return Arrays.equals(target, array);
    }

    default boolean matchAndJump(CharBuffer charBuffer, char... target) {
        char[] array = new char[target.length];
        charBuffer.get(array);
        return Arrays.equals(target, array);
    }
    /**
     * 从charBuffer当前位置开始，寻找匹配target，直到找到为止。会变更指针位置
     * @param charBuffer
     * @param target
     * @return
     */
    default boolean findMatch(CharBuffer charBuffer, char... target) {
        outer:
        while (charBuffer.hasRemaining()) {
            if (charBuffer.get() != target[0]) {
                continue;
            }
            charBuffer.mark();
            for (int i = 1; i < target.length; i++) {
                if (charBuffer.get() != target[i]) {
                    // 不匹配，需要从mark位置继续走while循环
                    charBuffer.reset();
                    continue outer;
                }
            }
            // 走到这里，说明匹配成功了，那么就到此结束。
            return true;
        }
        return false;
    }

    default boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z');
    }

    default boolean isNum(char c) {
        return c >= '0' && c <= '9';
    }

    default boolean isBlank(char c) {
        return c == '\t' || c == ' ';
    }

    default boolean isEnter(char c) {
        return c == '\n' || c == '\r';
    }

    /**
     * 后续的数字可以是 字母、数字、下划线
     * @param c
     * @return
     */
    default boolean isValid(char c) {
        return isLetter(c) || isNum(c) || c == '_';
    }
}
