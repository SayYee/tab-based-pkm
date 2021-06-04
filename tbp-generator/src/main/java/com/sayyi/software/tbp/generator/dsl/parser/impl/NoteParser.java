package com.sayyi.software.tbp.generator.dsl.parser.impl;

import com.sayyi.software.tbp.generator.dsl.parser.Parser;

import java.nio.CharBuffer;

/**
 * 针对相对复杂的程序设计方案：首先设计一个案例，仅仅针对这个案例来进行解析。然后再进行拆分、通用化设计。每次解决一部分问题就可以了。
 * 注释解析器
 */
public class NoteParser implements Parser<String> {

    private static final char[] SINGLE_LINE_NOTE = "//".toCharArray();
    private static final char[] SINGLE_LINE_NOTE_END = System.getProperty("line.separator").toCharArray();

    private static final char[] MULTIPLE_LINE_NOTE = "/*".toCharArray();
    private static final char[] MULTIPLE_LINE_NOTE_END = "*/".toCharArray();

    @Override
    public String parse(CharBuffer charBuffer) {
        int start = charBuffer.position();
        if (!match(charBuffer)) {
            throw new IllegalArgumentException("not march");
        }
        // 有两种注释的可能，// 和 /** */
        // 单行注释，直接跳过当前行数据就可以。
        if (match(charBuffer, SINGLE_LINE_NOTE)) {
            jumpLine(charBuffer);
        } else if (match(charBuffer, MULTIPLE_LINE_NOTE)) {
            // 多行注释
            matchNoteEnd(charBuffer);
        } else {
            throw new IllegalArgumentException("not march");
        }
        int end = charBuffer.position();
        // 尝试展示注释内容。作为测试使用
        char[] result = new char[end - start];
        charBuffer.position(start);
        charBuffer.get(result);
        return String.valueOf(result);
    }

    /**
     * 跳转到单行注释结尾
     * @param charBuffer
     * @return
     */
    private boolean jumpLine(CharBuffer charBuffer) {
        return findMatch(charBuffer, SINGLE_LINE_NOTE_END);
    }

    /**
     * 跳转到多行注释结尾
     * @param charBuffer
     * @return
     */
    private boolean matchNoteEnd(CharBuffer charBuffer) {
        // 结束条件为 */
        return findMatch(charBuffer, MULTIPLE_LINE_NOTE_END);
    }

    /**
     * 判断是否使用当前解析器
     * @param charBuffer
     * @return
     */
    @Override
    public boolean match(CharBuffer charBuffer) {
        return match(charBuffer, '/');
    }
}
