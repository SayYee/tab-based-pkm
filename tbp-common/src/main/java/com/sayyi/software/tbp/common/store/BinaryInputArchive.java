package com.sayyi.software.tbp.common.store;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 从 zookeeper的jute中拿过来的模块
 * @author SayYi
 */
@Slf4j
public class BinaryInputArchive implements InputArchive {

    private final DataInput in;

    public static BinaryInputArchive getArchive(InputStream strm) {
        return new BinaryInputArchive(new DataInputStream(strm));
    }

    public static BinaryInputArchive getArchive(byte[] data) {
        return new BinaryInputArchive(new DataInputStream(new ByteArrayInputStream(data)));
    }

    public static <B extends Record> void deserialize(B record, byte[] data) throws IOException {
        final BinaryInputArchive archive = new BinaryInputArchive(new DataInputStream(new ByteArrayInputStream(data)));
        archive.readRecord(record);
    }

    public static<B extends Record> List<B> deserialize(Class<B> recordClass, byte[] data) throws IOException, IllegalAccessException, InstantiationException {
        BinaryInputArchive archive = new BinaryInputArchive(new DataInputStream(new ByteArrayInputStream(data)));
        final int size = archive.readInt();
        List<B> array = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            B record = recordClass.newInstance();
            archive.readRecord(record);
            array.add(record);
        }
        return array;
    }

    public static<B extends Record> void deserialize(Collection<B> collection, Class<B> recordClass, byte[] data) throws IOException, IllegalAccessException, InstantiationException {
        BinaryInputArchive archive = new BinaryInputArchive(new DataInputStream(new ByteArrayInputStream(data)));
        final int size = archive.readInt();
        for (int i = 0; i < size; i++) {
            B record = recordClass.newInstance();
            archive.readRecord(record);
            collection.add(record);
        }
        log.debug("集合反序列化结果：{}", collection);
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
