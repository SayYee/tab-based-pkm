package com.sayyi.software.tbp.web.controller;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.TbpException;
import com.sayyi.software.tbp.core.PkmFunction;
import com.sayyi.software.tbp.web.common.ResultBean;
import com.sayyi.software.tbp.web.model.FileUpdateInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author SayYi
 */
@RestController
public class TbpController {

    @Autowired
    private PkmFunction pkmFunction;

    /**
     * 文件上传
     * @param file
     * @return
     * @throws IOException
     * @throws TbpException
     */
    @PostMapping("/upload")
    public ResultBean<Long> upload(@RequestParam("file") MultipartFile file) throws IOException, TbpException {
        FileMetadata fileMetadata = pkmFunction.upload(file.getOriginalFilename(), file.getInputStream());
        return ResultBean.ok(fileMetadata.getId());
    }

    @GetMapping("/query")
    public ResultBean<List<FileMetadata>> query(String tags, String filename) {
        Set<String> tagSet = tagStrToSet(tags);
        List<FileMetadata> fileMetadata = pkmFunction.listByNameAndTag(tagSet, filename);
        return ResultBean.ok(fileMetadata);
    }

    @GetMapping("/open/{id}")
    public ResultBean<Boolean> open(@PathVariable("id") long id) throws TbpException {
        pkmFunction.open(id);
        return ResultBean.ok(true);
    }

    @PutMapping("/update")
    public ResultBean<Boolean> update(@RequestBody FileUpdateInfo fileUpdateInfo) throws TbpException {
        long fileId = fileUpdateInfo.getId();
        String newName = fileUpdateInfo.getNewName();
        Set<String> tagSet = tagStrToSet(fileUpdateInfo.getTagStr());

        pkmFunction.rename(fileId, newName);
        pkmFunction.modifyTag(fileId, tagSet);
        return ResultBean.ok(true);
    }

    @DeleteMapping("/delete/{id}")
    public ResultBean<Boolean> delete(@PathVariable("id") long id) throws TbpException {
        // 我是为什么要用restful风格的，这不是把自己当智障吗？
        pkmFunction.delete(id);
        return ResultBean.ok(true);
    }

    @GetMapping("/tagMap")
    @CrossOrigin
    public void tagMap(HttpServletResponse response) throws IOException, TbpException {
        ServletOutputStream outputStream = response.getOutputStream();
        pkmFunction.tagMap(outputStream);
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
