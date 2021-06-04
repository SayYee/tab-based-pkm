// 使用模版生成，请不要手动修改
package com.sayyi.software.tbp.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController implements com.sayyi.software.tbp.web.api.FeignPkmFunction {

    @Autowired
    private com.sayyi.software.tbp.core.facade.PkmFunction pkmFunction;

@Override
public com.sayyi.software.tbp.common.FileMetadata upload (java.lang.String arg0, byte[] arg1) throws java.lang.Exception {
return pkmFunction.upload(arg0, arg1);}
@Override
public void addFileTag (long arg0, java.util.Set<java.lang.String> arg1) throws java.lang.Exception {
pkmFunction.addFileTag(arg0, arg1);}
@Override
public java.util.List<com.sayyi.software.tbp.common.model.TagInfo> listTags (java.util.Set<java.lang.String> arg0) throws java.lang.Exception {
return pkmFunction.listTags(arg0);}
@Override
public void deleteFileTag (long arg0, java.util.Set<java.lang.String> arg1) throws java.lang.Exception {
pkmFunction.deleteFileTag(arg0, arg1);}
@Override
public void modifyTag (long arg0, java.util.Set<java.lang.String> arg1) throws java.lang.Exception {
pkmFunction.modifyTag(arg0, arg1);}
@Override
public com.sayyi.software.tbp.common.FileMetadata getFileById (long arg0) throws java.lang.Exception {
return pkmFunction.getFileById(arg0);}
@Override
public java.util.List<com.sayyi.software.tbp.common.FileMetadata> listRecentOpened () throws java.lang.Exception {
return pkmFunction.listRecentOpened();}
@Override
public java.util.List<com.sayyi.software.tbp.common.FileMetadata> listByNameAndTag (java.util.Set<java.lang.String> arg0, java.lang.String arg1) throws java.lang.Exception {
return pkmFunction.listByNameAndTag(arg0, arg1);}
@Override
public void deleteTag (java.lang.String arg0) throws java.lang.Exception {
pkmFunction.deleteTag(arg0);}
@Override
public void renameTag (java.lang.String arg0, java.lang.String arg1) throws java.lang.Exception {
pkmFunction.renameTag(arg0, arg1);}
@Override
public void batchModifyTags (java.util.Set<java.lang.String> arg0, java.util.Set<java.lang.String> arg1) throws java.lang.Exception {
pkmFunction.batchModifyTags(arg0, arg1);}
@Override
public byte[] tagMap () throws java.lang.Exception {
return pkmFunction.tagMap();}
@Override
public java.util.List<java.lang.Long> listTreeIds () throws java.lang.Exception {
return pkmFunction.listTreeIds();}
@Override
public java.lang.String getCurrentTree () throws java.lang.Exception {
return pkmFunction.getCurrentTree();}
@Override
public java.lang.String getAssignTree (long arg0) throws java.lang.Exception {
return pkmFunction.getAssignTree(arg0);}
@Override
public long setTree (java.lang.String arg0) throws java.lang.Exception {
return pkmFunction.setTree(arg0);}
@Override
public void select (long arg0) throws java.lang.Exception {
pkmFunction.select(arg0);}
@Override
public com.sayyi.software.tbp.common.FileMetadata url (java.lang.String arg0, java.lang.String arg1, java.util.Set<java.lang.String> arg2) throws java.lang.Exception {
return pkmFunction.url(arg0, arg1, arg2);}
@Override
public void delete (long arg0) throws java.lang.Exception {
pkmFunction.delete(arg0);}
@Override
public com.sayyi.software.tbp.common.FileMetadata create (java.lang.String arg0, java.util.Set<java.lang.String> arg1) throws java.lang.Exception {
return pkmFunction.create(arg0, arg1);}
@Override
public com.sayyi.software.tbp.common.FileMetadata copy (java.lang.String arg0, java.util.Set<java.lang.String> arg1) throws java.lang.Exception {
return pkmFunction.copy(arg0, arg1);}
@Override
public void rename (long arg0, java.lang.String arg1) throws java.lang.Exception {
pkmFunction.rename(arg0, arg1);}
@Override
public void open (long arg0) throws java.lang.Exception {
pkmFunction.open(arg0);}
}