package com.sayyi.software.tbp.ui.api;

import com.sayyi.software.tbp.ui.api.model.ObservableMetadata;
import com.sayyi.software.tbp.common.FileMetadata;

public class File2ObservableConverter{

    public static ObservableMetadata convert(FileMetadata fileMetadata) {
        return new ObservableMetadata(
                fileMetadata.getId(),
                fileMetadata.getFilename(),
                fileMetadata.getResourceType(),
                fileMetadata.getTags(),
                fileMetadata.getLastOpenTime());
    }
}
