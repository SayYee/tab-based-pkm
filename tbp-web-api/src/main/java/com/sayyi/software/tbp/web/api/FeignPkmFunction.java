// 使用模版生成，请不要手动修改
package com.sayyi.software.tbp.web.api;

import org.springframework.web.bind.annotation.*;

@RequestMapping("/function")
public interface FeignPkmFunction extends com.sayyi.software.tbp.core.facade.PkmFunction {
@GetMapping("/upload")
@Override
com.sayyi.software.tbp.common.FileMetadata upload (@RequestParam("arg0")java.lang.String arg0, @RequestParam("arg1")byte[] arg1) throws java.lang.Exception;
@GetMapping("/addFileTag")
@Override
void addFileTag (@RequestParam("arg0")long arg0, @RequestParam("arg1")java.util.Set<java.lang.String> arg1) throws java.lang.Exception;
@GetMapping("/listTags")
@Override
java.util.List<com.sayyi.software.tbp.common.model.TagInfo> listTags (@RequestParam("arg0")java.util.Set<java.lang.String> arg0) throws java.lang.Exception;
@GetMapping("/deleteFileTag")
@Override
void deleteFileTag (@RequestParam("arg0")long arg0, @RequestParam("arg1")java.util.Set<java.lang.String> arg1) throws java.lang.Exception;
@GetMapping("/modifyTag")
@Override
void modifyTag (@RequestParam("arg0")long arg0, @RequestParam("arg1")java.util.Set<java.lang.String> arg1) throws java.lang.Exception;
@GetMapping("/getFileById")
@Override
com.sayyi.software.tbp.common.FileMetadata getFileById (@RequestParam("arg0")long arg0) throws java.lang.Exception;
@GetMapping("/listRecentOpened")
@Override
java.util.List<com.sayyi.software.tbp.common.FileMetadata> listRecentOpened () throws java.lang.Exception;
@GetMapping("/listByNameAndTag")
@Override
java.util.List<com.sayyi.software.tbp.common.FileMetadata> listByNameAndTag (@RequestParam("arg0")java.util.Set<java.lang.String> arg0, @RequestParam("arg1")java.lang.String arg1) throws java.lang.Exception;
@GetMapping("/deleteTag")
@Override
void deleteTag (@RequestParam("arg0")java.lang.String arg0) throws java.lang.Exception;
@GetMapping("/renameTag")
@Override
void renameTag (@RequestParam("arg0")java.lang.String arg0, @RequestParam("arg1")java.lang.String arg1) throws java.lang.Exception;
@GetMapping("/batchModifyTags")
@Override
void batchModifyTags (@RequestParam("arg0")java.util.Set<java.lang.String> arg0, @RequestParam("arg1")java.util.Set<java.lang.String> arg1) throws java.lang.Exception;
@GetMapping("/tagMap")
@Override
byte[] tagMap () throws java.lang.Exception;
@GetMapping("/listTreeIds")
@Override
java.util.List<java.lang.Long> listTreeIds () throws java.lang.Exception;
@GetMapping("/getCurrentTree")
@Override
java.lang.String getCurrentTree () throws java.lang.Exception;
@GetMapping("/getAssignTree")
@Override
java.lang.String getAssignTree (@RequestParam("arg0")long arg0) throws java.lang.Exception;
@GetMapping("/setTree")
@Override
long setTree (@RequestParam("arg0")java.lang.String arg0) throws java.lang.Exception;
@GetMapping("/select")
@Override
void select (@RequestParam("arg0")long arg0) throws java.lang.Exception;
@GetMapping("/url")
@Override
com.sayyi.software.tbp.common.FileMetadata url (@RequestParam("arg0")java.lang.String arg0, @RequestParam("arg1")java.lang.String arg1, @RequestParam("arg2")java.util.Set<java.lang.String> arg2) throws java.lang.Exception;
@GetMapping("/delete")
@Override
void delete (@RequestParam("arg0")long arg0) throws java.lang.Exception;
@GetMapping("/create")
@Override
com.sayyi.software.tbp.common.FileMetadata create (@RequestParam("arg0")java.lang.String arg0, @RequestParam("arg1")java.util.Set<java.lang.String> arg1) throws java.lang.Exception;
@GetMapping("/copy")
@Override
com.sayyi.software.tbp.common.FileMetadata copy (@RequestParam("arg0")java.lang.String arg0, @RequestParam("arg1")java.util.Set<java.lang.String> arg1) throws java.lang.Exception;
@GetMapping("/rename")
@Override
void rename (@RequestParam("arg0")long arg0, @RequestParam("arg1")java.lang.String arg1) throws java.lang.Exception;
@GetMapping("/open")
@Override
void open (@RequestParam("arg0")long arg0) throws java.lang.Exception;
}