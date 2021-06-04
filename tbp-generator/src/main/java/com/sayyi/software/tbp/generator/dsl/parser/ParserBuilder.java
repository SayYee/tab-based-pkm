package com.sayyi.software.tbp.generator.dsl.parser;

import java.nio.CharBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;

public class ParserBuilder {

    private final LinkedList<ParserLink> links = new LinkedList<>();
    private ParserLink currentLink;

    public ParserBuilder() {
        next();
    }

    /**
     * 添加解析器
     * @param parser
     * @param <T>
     * @return
     */
    public <T> ParserBuilder add(Parser<T> parser) {
        currentLink.add(new ParseAndDeal<>(parser));
        return this;
    }

    public <T> ParserBuilder add(Parser<T> parser, Consumer<T> consumer) {
        currentLink.add(new ParseAndDeal<>(parser, consumer));
        return this;
    }

    /**
     * 切换解析器链条
     * @return
     */
    public ParserBuilder next() {
        currentLink = new ParserLink();
        links.add(currentLink);
        return this;
    }

    public void execute(CharBuffer charBuffer) {
        for (ParserLink parserLink : links) {
            // 取出一个链条中的所有 parser，然后进行匹配、反复处理。直到异常报错，或者满足跳出循环条件。
            // 匹配链条当中的最后一个node，为循环跳出条件，因此最后一个node只会执行一次。但是前边的node，只要匹配到就会执行
            LinkedList<ParseAndDeal<?>> parsers = parserLink.get();
            ParseAndDeal<?> parser;

            outer:
            while (true) {
                // 每次都从头开始遍历
                Iterator<ParseAndDeal<?>> iterator = parsers.iterator();
                boolean isFinished = false;
                while (iterator.hasNext()) {
                    parser = iterator.next();
//                    System.out.println(parser);
                    // 是有完成遍历了（最后一个节点）
                    isFinished = !iterator.hasNext();
                    if (parser.match(charBuffer)) {
                        parser.exec(charBuffer);
                        if (isFinished) {
                            // 只有最后一个节点，可以跳出循环
                            break outer;
                        } else {
                            // 匹配了，需要进入下个循环，继续匹配
                            break;
                        }
                    }
                }
                // 全部遍历，没有合适的，报错
                if (isFinished) {
                    throw new IllegalArgumentException();
                }
            }
            // 当前阶段处理完成，直接离开。
        }
    }

    private static class ParserLink {
        private final LinkedList<ParseAndDeal<?>> parsers = new LinkedList<>();

        ParserLink add(ParseAndDeal<?> parser) {
            parsers.add(parser);
            return this;
        }

        LinkedList<ParseAndDeal<?>> get() {
            return parsers;
        }
    }
}
