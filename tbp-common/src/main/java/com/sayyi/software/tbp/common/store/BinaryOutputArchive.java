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

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.TreeMap;

/**
 * zookeeper的jute中拿过来的模块
 * @author SayYi
 */
public class BinaryOutputArchive implements OutputArchive {
    private ByteBuffer bb = ByteBuffer.allocate(1024);

    private final DataOutput out;

    public static BinaryOutputArchive getArchive(OutputStream strm) {
        return new BinaryOutputArchive(new DataOutputStream(strm));
    }

    /**
     * Creates a new instance of BinaryOutputArchive.
     */
    public BinaryOutputArchive(DataOutput out) {
        this.out = out;
    }

    @Override
    public void writeByte(byte b) throws IOException {
        out.writeByte(b);
    }

    @Override
    public void writeBool(boolean b) throws IOException {
        out.writeBoolean(b);
    }

    @Override
    public void writeInt(int i) throws IOException {
        out.writeInt(i);
    }

    @Override
    public void writeLong(long l) throws IOException {
        out.writeLong(l);
    }

    @Override
    public void writeFloat(float f) throws IOException {
        out.writeFloat(f);
    }

    @Override
    public void writeDouble(double d) throws IOException {
        out.writeDouble(d);
    }

    /**
     * create our own char encoder to utf8. This is faster
     * then string.getbytes(UTF8).
     *
     * @param s the string to encode into utf8
     * @return utf8 byte sequence.
     */
    private ByteBuffer stringToByteBuffer(CharSequence s) {
        bb.clear();
        final int len = s.length();
        for (int i = 0; i < len; i++) {
            if (bb.remaining() < 3) {
                ByteBuffer n = ByteBuffer.allocate(bb.capacity() << 1);
                bb.flip();
                n.put(bb);
                bb = n;
            }
            char c = s.charAt(i);
            if (c < 0x80) {
                bb.put((byte) c);
            } else if (c < 0x800) {
                bb.put((byte) (0xc0 | (c >> 6)));
                bb.put((byte) (0x80 | (c & 0x3f)));
            } else {
                bb.put((byte) (0xe0 | (c >> 12)));
                bb.put((byte) (0x80 | ((c >> 6) & 0x3f)));
                bb.put((byte) (0x80 | (c & 0x3f)));
            }
        }
        bb.flip();
        return bb;
    }

    @Override
    public void writeString(String s) throws IOException {
        if (s == null) {
            writeInt(-1);
            return;
        }
        ByteBuffer bb = stringToByteBuffer(s);
        writeInt(bb.remaining());
        out.write(bb.array(), bb.position(), bb.limit());
    }

    @Override
    public void writeBuffer(byte[] barr)
            throws IOException {
        if (barr == null) {
            out.writeInt(-1);
            return;
        }
        out.writeInt(barr.length);
        out.write(barr);
    }

    @Override
    public void writeRecord(Record r) throws IOException {
        r.serialize(this);
    }

}
