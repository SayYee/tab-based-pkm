package com.sayyi.software.tbp.web.controller;

import com.sayyi.software.tbp.cli.CmdExecutor;
import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.TbpException;
import com.sayyi.software.tbp.core.facade.PkmFunction;
import com.sayyi.software.tbp.web.common.ResultBean;
import com.sayyi.software.tbp.web.model.FileUpdateInfo;
import com.sayyi.software.tbp.web.model.TagRenameInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author SayYi
 */
@RestController
public class TbpController {

    @Autowired
    private PkmFunction pkmFunction;

    @PostMapping("/copy")
    public ResultBean<Long> copyFile(String path, String tagStr) throws Exception {
        return copy(path, tagStr);
    }
    /**
     * 通过输入的文件路径，将文件纳入管理。
     * 这里的path，可以是文件夹
     * @param path
     * @return
     */
    // 用get吧，别给自己找麻烦了
    @GetMapping("/copy")
    public ResultBean<Long> copy(String path, String tagStr) throws Exception {
        if (null == path || "".equals(path.trim())) {
            return ResultBean.error("文件路径为空");
        }
        Set<String> tagSet = tagStrToSet(tagStr);
        FileMetadata fileMetadata = pkmFunction.copy(path, tagSet);
        return ResultBean.ok(fileMetadata.getId());
    }

    /**
     * 文件上传
     * @param file
     * @return
     * @throws IOException
     * @throws TbpException
     */
    @PostMapping("/upload")
    public ResultBean<Long> upload(@RequestParam("file") MultipartFile file) throws Exception {
        FileMetadata fileMetadata = pkmFunction.upload(file.getOriginalFilename(), file.getBytes());
        return ResultBean.ok(fileMetadata.getId());
    }

    @PostMapping("/create")
    public ResultBean<FileMetadata> create(String filename, String tagStr) throws Exception {
        Set<String> tagSet = tagStrToSet(tagStr);
        FileMetadata fileMetadata = pkmFunction.create(filename, tagSet);
        pkmFunction.open(fileMetadata.getId());
        return ResultBean.ok(fileMetadata);
    }

    @PostMapping("/url")
    public ResultBean<FileMetadata> url(String name, String url, String tagStr) throws Exception {
        Set<String> tagSet = tagStrToSet(tagStr);
        FileMetadata fileMetadata = pkmFunction.url(name, url, tagSet);
//        pkmFunction.open(fileMetadata.getId());
        return ResultBean.ok(fileMetadata);
    }

    /**
     * 通过标签、文件名查询文件
     * @param tags  . 分隔的标签
     * @param filename
     * @return
     */
    @GetMapping("/query")
    public ResultBean<List<FileMetadata>> query(String tags, String filename) throws Exception {
        Set<String> tagSet = tagStrToSet(tags);
        List<FileMetadata> fileMetadata = pkmFunction.listByNameAndTag(tagSet, filename);
        return ResultBean.ok(fileMetadata);
    }

    /**
     * 打开文件
     * @param id    文件id
     * @return
     * @throws TbpException
     */
    @GetMapping("/open/{id}")
    public ResultBean<Boolean> open(@PathVariable("id") long id) throws Exception {
        pkmFunction.open(id);
        return ResultBean.ok(true);
    }

    /**
     * 更新文件元数据（名称、标签）
     * @param fileUpdateInfo
     * @return
     * @throws TbpException
     */
    @PutMapping("/update")
    public ResultBean<Boolean> update(@RequestBody FileUpdateInfo fileUpdateInfo) throws Exception {
        long fileId = fileUpdateInfo.getId();
        String newName = fileUpdateInfo.getNewName();
        String newLocation = fileUpdateInfo.getNewLocation();
        Set<String> tagSet = fileUpdateInfo.getTags();

        pkmFunction.rename(fileId, newName, newLocation);
        pkmFunction.modifyTag(fileId, tagSet);
        return ResultBean.ok(true);
    }

    /**
     * 删除文件
     * @param id    文件id
     * @return
     * @throws TbpException
     */
    @DeleteMapping("/delete/{id}")
    public ResultBean<Boolean> delete(@PathVariable("id") long id) throws Exception {
        // 我是为什么要用restful风格的，这不是把自己当智障吗？
        pkmFunction.delete(id);
        return ResultBean.ok(true);
    }

    @PutMapping("/modifyTag")
    public ResultBean<Boolean> modifyTag(String tag, String newTag) throws Exception {
        pkmFunction.renameTag(tag, newTag);
        return ResultBean.ok(true);
    }

    /**
     * 标签重命名。允许重命名为已经存在的标签
     * @param tagRenameInfo
     * @return
     * @throws TbpException
     */
    @PutMapping("/renameTag")
    public ResultBean<Boolean> renameTag(@RequestBody TagRenameInfo tagRenameInfo) throws Exception {
        pkmFunction.renameTag(tagRenameInfo.getTag(), tagRenameInfo.getNewTag());
        return ResultBean.ok(true);
    }

    /**
     * 删除标签
     * @param tag
     * @return
     * @throws TbpException
     */
    @DeleteMapping("/deleteTag/{tag}")
    public ResultBean<Boolean> deleteTag(@PathVariable("tag") String tag) throws Exception {
        pkmFunction.deleteTag(tag);
        return ResultBean.ok(true);
    }

    /**
     * 获取标签图
     * @param response
     * @throws IOException
     * @throws TbpException
     */
    @GetMapping("/tagMap")
    public void tagMap(HttpServletResponse response) throws Exception {
        ServletOutputStream outputStream = response.getOutputStream();
        byte[] bytes = pkmFunction.tagMap();
        outputStream.write(bytes);
        outputStream.flush();
    }

    @GetMapping("/listTreeIds")
    public ResultBean<List<Long>> listTreeIds() throws Exception {
        return ResultBean.ok(pkmFunction.listTreeIds());
    }

    /** jstree 不支持数据的二次加工 */
    @GetMapping("/getCurrentTree")
    public ResultBean<String> getCurrentTree() throws Exception {
        return ResultBean.ok(pkmFunction.getCurrentTree());
    }
    @GetMapping("/getAssignTree")
    public ResultBean<String> getAssignTree(long id) throws Exception {
        if (id == -1) {
            return ResultBean.ok(pkmFunction.getCurrentTree());
        } else {
            return ResultBean.ok(pkmFunction.getAssignTree(id));
        }
    }

    @PutMapping("/setTree")
    public ResultBean<Long> setTree(String treeStr) throws Exception {
        return ResultBean.ok(pkmFunction.setTree(treeStr));
    }

    @Autowired
    private CmdExecutor cmdExecutor;

    @GetMapping("/cmd")
    public ResultBean<Object> cmd(String cmdStr) throws Exception {
        if (cmdStr == null || "".equals(cmdStr)) {
            return ResultBean.ok("请输入命令");
        }
        String[] split = cmdStr.split(" ");
        // 去除空字符串
        split = Arrays.stream(split).filter(s -> s.length() != 0).toArray(String[]::new);
        return ResultBean.ok(cmdExecutor.execute(split));
    }

    @GetMapping("/cmdInfo")
    public ResultBean<Map<String, String>> cmdInfo() throws Exception {
        return ResultBean.ok(cmdExecutor.commandUsageInfo());
    }

    private Set<String> tagStrToSet(String tagStr) {
        if (tagStr == null || "".equals(tagStr.trim())) {
            return new HashSet<>();
        }
        tagStr = tagStr.trim();
        final String[] tagArray = tagStr.split("\\.");
        Set<String> tagSet = new HashSet<>(tagArray.length);
        tagSet.addAll(Arrays.asList(tagArray));
        return tagSet;
    }
}
