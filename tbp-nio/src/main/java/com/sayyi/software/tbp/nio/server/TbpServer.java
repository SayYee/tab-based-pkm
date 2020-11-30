package com.sayyi.software.tbp.nio.server;

import com.sayyi.software.tbp.common.TbpConfig;
import com.sayyi.software.tbp.common.TbpConfigParse;
import com.sayyi.software.tbp.core.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author SayYi
 */
@Slf4j
public class TbpServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        TbpConfig tbpConfig = new TbpConfigParse(args[0]);

        FileManager fileManager = new FileManager(tbpConfig.getStoreDir());
        MetadataFunction metadataFunction = new MetadataManager();
        DbFunction dbFunction = new FileBasedDbManager(tbpConfig.getSnapDir());
        PkmService pkmService = new PkmServiceImpl(fileManager, metadataFunction, dbFunction);

        SelectorThread selectorThread = new SelectorThread(pkmService);
        AcceptorThread acceptorThread = new AcceptorThread(tbpConfig.getPort(), selectorThread);
        selectorThread.start();
        acceptorThread.start();

        acceptorThread.join();
    }

}
