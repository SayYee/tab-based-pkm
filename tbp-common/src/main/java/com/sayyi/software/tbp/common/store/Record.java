package com.sayyi.software.tbp.common.store;

import java.io.IOException;

/**
 * zookeeper的jute中拿过来的模块
 * @author SayYi
 */
public interface Record {

    void serialize(OutputArchive archive) throws IOException;
    void deserialize(InputArchive archive) throws IOException;
}
