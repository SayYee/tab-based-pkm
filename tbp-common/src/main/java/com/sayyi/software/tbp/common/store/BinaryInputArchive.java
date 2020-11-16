package com.sayyi.software.tbp.common.store;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 从 zookeeper的jute中拿过来的模块
 * @author SayYi
 */
public class BinaryInputArchive implements InputArchive {

    private final DataInput in;

    public static BinaryInputArchive getArchive(InputStream strm) {
        return new BinaryInputArchive(new DataInputStream(strm));
    }

    /**
     * Creates a new instance of BinaryInputArchive.
     */
    public BinaryInputArchive(DataInput in) {
        this.in = in;
    }

    @Override
    public byte readByte() throws IOException {
        return in.readByte();
    }

    @Override
    public boolean readBool() throws IOException {
        return in.readBoolean();
    }

    @Override
    public int readInt() throws IOException {
        return in.readInt();
    }

    @Override
    public long readLong() throws IOException {
        return in.readLong();
    }

    @Override
    public float readFloat() throws IOException {
        return in.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return in.readDouble();
    }

    @Override
    public String readString() throws IOException {
        int len = in.readInt();
        if (len == -1) {
            return null;
        }
        byte[] b = new byte[len];
        in.readFully(b);
        return new String(b, StandardCharsets.UTF_8);
    }

    @Override
    public byte[] readBuffer() throws IOException {
        int len = readInt();
        if (len == -1) {
            return null;
        }
        byte[] arr = new byte[len];
        in.readFully(arr);
        return arr;
    }

    @Override
    public void readRecord(Record r) throws IOException {
        r.deserialize(this);
    }

}
