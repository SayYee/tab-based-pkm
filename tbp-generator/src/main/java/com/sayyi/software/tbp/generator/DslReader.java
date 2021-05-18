package com.sayyi.software.tbp.generator;

import com.sayyi.software.tbp.generator.model.ModuleInfo;
import com.sayyi.software.tbp.generator.parser.impl.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;

public class DslReader {

    private final DslParser dslParser;

    public DslReader() {
        // 解析器都是无状态的，可以反复使用
        NoteParser noteParser = new NoteParser();
        StringParser stringParser = new StringParser();
        BlankParser blankParser = new BlankParser();
        EnterParser enterParser = new EnterParser();
        FieldParser fieldParser = new FieldParser(stringParser, blankParser);
        ClassParser classParser = new ClassParser(stringParser, fieldParser, noteParser, blankParser, enterParser);
        ModuleParser moduleParser = new ModuleParser(stringParser, classParser, noteParser, blankParser, enterParser);
        dslParser = new DslParser(moduleParser, noteParser, blankParser, enterParser);
    }
    /**
     * 从指定的dsl文件中，解析类信息
     * @param path  绝对路径为项目根目录。相对路径为相对当前类所在文件夹
     * @return
     * @throws IOException
     */
    public List<ModuleInfo> parse(String path) throws IOException {
        // 应该怎么解析呢？逐行解析。读取每一行的数据，
        File file = new File(Objects.requireNonNull(DslReader.class.getResource(path)).getPath());
        try (FileChannel fileChannel = FileChannel.open(file.toPath(), StandardOpenOption.READ)) {
            // 1M 空间，应该怎么都够用了
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024);
            fileChannel.read(byteBuffer);
            byteBuffer.flip();
            CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
            CharBuffer charBuffer = decoder.decode(byteBuffer);
            return dslParser.parse(charBuffer);
        }
    }

}
