package com.sayyi.software.tbp.generator.dsl.parser;

import java.nio.CharBuffer;
import java.util.function.Consumer;

/**
 * 携带consumer的parser
 * @param <T>
 */
public class ParseAndDeal<T> implements Parser<T> {

    private final Parser<T> parser;
    private Consumer<T> consumer;

    public ParseAndDeal(Parser<T> parser) {
        this.parser = parser;
    }

    public ParseAndDeal(Parser<T> parser, Consumer<T> consumer) {
        this.parser = parser;
        this.consumer = consumer;
    }

    public void exec(CharBuffer charBuffer) {
        T result = parser.parse(charBuffer);
        if (consumer != null) {
            consumer.accept(result);
        }
    }

    @Override
    public T parse(CharBuffer charBuffer) {
        return parser.parse(charBuffer);
    }

    @Override
    public boolean match(CharBuffer charBuffer) {
        return parser.match(charBuffer);
    }

    @Override
    public String toString() {
        return "ParseAndDeal{" +
                "parser=" + parser +
                ", consumer=" + consumer +
                '}';
    }
}
