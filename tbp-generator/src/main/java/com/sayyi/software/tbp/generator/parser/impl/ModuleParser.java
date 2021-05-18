package com.sayyi.software.tbp.generator.parser.impl;

import com.sayyi.software.tbp.generator.model.ClassInfo;
import com.sayyi.software.tbp.generator.model.ModuleInfo;
import com.sayyi.software.tbp.generator.parser.Parser;
import com.sayyi.software.tbp.generator.parser.ParserBuilder;
import com.sun.tools.javac.util.Assert;

import java.nio.CharBuffer;
import java.util.*;

/**
 * module信息解析
 */
public class ModuleParser implements Parser<ModuleInfo> {

    private final StringParser stringParser;
    private final ModuleNameParser moduleNameParser;
    private final ClassParser classParser;
    private final NoteParser noteParser;
    private final BlankParser blankParser;
    private final EnterParser enterParser;

    public ModuleParser(StringParser stringParser,
                        ClassParser classParser,
                        NoteParser noteParser,
                        BlankParser blankParser,
                        EnterParser enterParser) {
        this.stringParser = stringParser;
        this.moduleNameParser = new ModuleNameParser(stringParser);
        this.classParser = classParser;
        this.noteParser = noteParser;
        this.blankParser = blankParser;
        this.enterParser = enterParser;
    }

    @Override
    public ModuleInfo parse(CharBuffer charBuffer) {
        ModuleInfo moduleInfo = new ModuleInfo();
        List<ClassInfo> classInfos = new LinkedList<>();
        moduleInfo.setClassInfos(classInfos);

        // 如果想要把 builder的组装过程拉出来，需要考虑moduleInfo对象的处理。每次生成的moduleInfo需要是不同的才行。
        // 怎么破？

        // 1、module开头
        // 2、空白
        // 3、module name
        // 4、空白、换行、花括号
        // 5、换行、空白、注释、class、花括号
        ParserBuilder parserBuilder = new ParserBuilder();
        parserBuilder.add(stringParser, s -> Assert.check("module".equals(s), "module开头"))
                .next()
                .add(blankParser, c -> Assert.check(c > 0, "必须有空格"))
                .next()
                .add(moduleNameParser, moduleInfo::setModuleName)
                .next()
                .add(blankParser)
                .add(enterParser)
                .add(CharBuffer::get, c -> Assert.check(c == '{', "module 必须花括号开头"))
                .next()
                .add(blankParser)
                .add(enterParser)
                .add(noteParser)
                .add(classParser, classInfos::add)
                .add(CharBuffer::get, c -> Assert.check(c == '}', "module 必须花括号结束"));
        parserBuilder.execute(charBuffer);
        return moduleInfo;
    }

    @Override
    public boolean match(CharBuffer charBuffer) {
        return match(charBuffer, 'm');
    }

    /**
     * module name 专用解析器
     */
    private static class ModuleNameParser implements Parser<String> {

        private final StringParser stringParser;

        public ModuleNameParser(StringParser stringParser) {
            this.stringParser = stringParser;
        }

        @Override
        public String parse(CharBuffer charBuffer) {
            StringBuilder stringBuilder = new StringBuilder();
            while (charBuffer.hasRemaining()) {
                stringBuilder.append(stringParser.parse(charBuffer));
                if (match(charBuffer, '.')) {
                    charBuffer.get();
                    stringBuilder.append('.');
                } else {
                    break;
                }
            }
            if (!charBuffer.hasRemaining()){
                throw new IllegalArgumentException();
            }
            return stringBuilder.toString();
        }

        @Override
        public boolean match(CharBuffer charBuffer) {
            return stringParser.match(charBuffer);
        }
    }
}
