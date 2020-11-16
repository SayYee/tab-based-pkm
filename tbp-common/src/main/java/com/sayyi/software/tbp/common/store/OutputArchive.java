/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sayyi.software.tbp.common.store;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

/**
 * zookeeper的jute中拿过来的模块
 * @author SayYi
 */
public interface OutputArchive {

    void writeByte(byte b) throws IOException;

    void writeBool(boolean b) throws IOException;

    void writeInt(int i) throws IOException;

    void writeLong(long l) throws IOException;

    void writeFloat(float f) throws IOException;

    void writeDouble(double d) throws IOException;

    void writeString(String s) throws IOException;

    void writeBuffer(byte[] buf) throws IOException;

    void writeRecord(Record r) throws IOException;

}
