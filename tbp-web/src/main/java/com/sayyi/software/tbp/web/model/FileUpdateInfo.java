package com.sayyi.software.tbp.web.model;

import lombok.Data;

import java.util.Set;

/**
 * @author SayYi
 */
@Data
public class FileUpdateInfo {

    private long id;
    private String newName;
    private String newLocation;
    private Set<String> tags;
}
