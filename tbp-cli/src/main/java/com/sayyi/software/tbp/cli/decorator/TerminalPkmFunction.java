package com.sayyi.software.tbp.cli.decorator;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.model.TagInfo;
import com.sayyi.software.tbp.core.facade.PkmFunction;

import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

/**
 * 终端调用的pkmFunction装饰器。
 * 为了使用统一的命令行处理类，供终端调用、方法调用，提供了进一步的封装。
 * @author SayYi
 */
public class TerminalPkmFunction implements PkmFunction {

    private final PrintWriter printWriter;
    private final PkmFunction pkmFunction;

    public TerminalPkmFunction(PkmFunction pkmFunction, PrintWriter printWriter) {
        this.printWriter = printWriter;
        this.pkmFunction = pkmFunction;
    }

    public static PkmFunction create(PkmFunction pkmFunction, PrintWriter printWriter) {
        return new TerminalPkmFunction(pkmFunction, printWriter);
    }

    @Override
    public FileMetadata upload(String filename, byte[] data) throws Exception {
        return pkmFunction.upload(filename, data);
    }

    @Override
    public FileMetadata copy(String filepath, Set<String> tags) throws Exception {
        return pkmFunction.copy(filepath, tags);
    }

    @Override
    public FileMetadata create(String filename, Set<String> tags) throws Exception {
        return pkmFunction.create(filename, tags);
    }

    @Override
    public FileMetadata url(String name, String url, Set<String> tags) throws Exception {
        return pkmFunction.url(name, url, tags);
    }

    @Override
    public void rename(long fileId, String newName) throws Exception {
        pkmFunction.rename(fileId, newName);
    }

    @Override
    public void addFileTag(long fileId, Set<String> newTags) throws Exception {
        pkmFunction.addFileTag(fileId, newTags);
    }

    @Override
    public void deleteFileTag(long fileId, Set<String> newTags) throws Exception {
        pkmFunction.deleteFileTag(fileId, newTags);
    }

    @Override
    public void modifyTag(long fileId, Set<String> newTags) throws Exception {
        pkmFunction.modifyTag(fileId, newTags);
    }

    @Override
    public void open(long fileId) throws Exception {
        pkmFunction.open(fileId);
    }

    @Override
    public void select(long fileId) throws Exception {
        pkmFunction.select(fileId);
    }

    @Override
    public void delete(long fileId) throws Exception {
        pkmFunction.delete(fileId);
    }

    @Override
    public FileMetadata getFileById(long fileId) throws Exception {
        return pkmFunction.getFileById(fileId);
    }

    @Override
    public List<FileMetadata> listRecentOpened() throws Exception {
        return pkmFunction.listRecentOpened();
    }

    @Override
    public List<FileMetadata> listByNameAndTag(Set<String> tags, String filenameReg) throws Exception {
        return pkmFunction.listByNameAndTag(tags, filenameReg);
    }

    @Override
    public void deleteTag(String tagName) throws Exception {
        pkmFunction.deleteTag(tagName);
    }

    @Override
    public void renameTag(String tagName, String newName) throws Exception {
        pkmFunction.renameTag(tagName, newName);
    }

    @Override
    public void batchModifyTags(Set<String> tags, Set<String> newTags) throws Exception {
        pkmFunction.batchModifyTags(tags, newTags);
    }

    @Override
    public List<TagInfo> listTags(Set<String> tags) throws Exception {
        return pkmFunction.listTags(tags);
    }

    @Override
    public byte[] tagMap() throws Exception {
        return pkmFunction.tagMap();
    }

    @Override
    public List<Long> listTreeIds() throws Exception {
        return pkmFunction.listTreeIds();
    }

    @Override
    public String getCurrentTree() throws Exception {
        return pkmFunction.getCurrentTree();
    }

    @Override
    public String getAssignTree(long id) throws Exception {
        return pkmFunction.getAssignTree(id);
    }

    @Override
    public long setTree(String treeStr) throws Exception {
        return pkmFunction.setTree(treeStr);
    }
}
