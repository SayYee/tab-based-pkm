package com.sayyi.software.tbp.cli.util;

import com.sayyi.software.tbp.common.TagInfo;
import com.sayyi.software.tbp.common.constant.RequestType;
import com.sayyi.software.tbp.common.flow.QueryTagRequest;
import com.sayyi.software.tbp.nio.client.TbpClient;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * @author SayYi
 */
public class RequestSenderTest {

    @Test
    public void test() throws IOException, InterruptedException, IllegalAccessException, InstantiationException {
        TbpClient client = new TbpClient(9000);
        client.start();
        RequestSender sender = new RequestSender(client);

        QueryTagRequest queryTagRequest = new QueryTagRequest();
        queryTagRequest.setTags(new HashSet<>());

        List<TagInfo> tagInfos = new LinkedList<>();
        sender.sendForCollection(RequestType.LIST_TAGS, queryTagRequest, TagInfo.class, tagInfos);
        PrintWriter printWriter = new PrintWriter(System.out);
        PrintUtil.printTagList(printWriter, tagInfos.toArray(new TagInfo[0]));
    }
}
