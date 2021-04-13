package com.sayyi.software.tbp.core.tool;

import com.sayyi.software.tbp.common.flow.Request;
import com.sayyi.software.tbp.common.flow.Response;
import com.sayyi.software.tbp.common.store.BinaryInputArchive;
import com.sayyi.software.tbp.core.flow.ProcessorPipeline;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * 打印request文件内容
 */
public class PrintRequestFile {

    public static void main(String[] args) throws IOException {
//        String file = "D:\\pkm\\snap\\request-2260-冲突-say意_Win10";
        String file = "D:\\pkm\\snap\\request-2260";
        final byte[] bytes = Files.readAllBytes(new File(file).toPath());
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        BinaryInputArchive archive = BinaryInputArchive.getArchive(byteArrayInputStream);

        PrintProcessor printProcessor = new PrintProcessor();
        ProcessorPipeline processorPipeline = new ProcessorPipeline();
        processorPipeline.addFirst(printProcessor);
        Request request = new Request();
        Response response = new Response();
        while (byteArrayInputStream.available() > 0) {
            archive.readRecord(request);
            System.out.println(request);
            processorPipeline.deal(request, response);
        }

    }

}
