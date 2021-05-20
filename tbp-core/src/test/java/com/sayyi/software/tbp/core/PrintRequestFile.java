package com.sayyi.software.tbp.core;

import com.sayyi.software.tbp.common.flow.Request;
import com.sayyi.software.tbp.common.flow.Response;
import com.sayyi.software.tbp.common.store.BinaryInputArchive;
import com.sayyi.software.tbp.core.flow.ProcessorPipeline;
import com.sayyi.software.tbp.core.flow.processor.FinalProcessor;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.mockito.Mockito.mock;

/**
 * @author xuchuang
 * @date 2021/5/20
 */
public class PrintRequestFile {

    FinalProcessor finalProcessor;

    @Before
    public void prepare() {
        MetadataFunction metadataFunction = mock(MetadataFunction.class);
        TagTreeFunction tagTreeFunction = mock(TagTreeFunction.class);
        finalProcessor = new FinalProcessor(metadataFunction, tagTreeFunction);
    }

    @Test
    public void print_request_file() throws IOException {
        String file = "D:\\pkm\\snap\\request-2260";
        final byte[] bytes = Files.readAllBytes(new File(file).toPath());
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        BinaryInputArchive archive = BinaryInputArchive.getArchive(byteArrayInputStream);

        ProcessorPipeline processorPipeline = new ProcessorPipeline();
        processorPipeline.addFirst(finalProcessor);
        Request request = new Request();
        Response response = new Response();
        while (byteArrayInputStream.available() > 0) {
            archive.readRecord(request);
            System.out.println(request);
            processorPipeline.deal(request, response);
        }
    }
}
