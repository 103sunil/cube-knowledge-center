package com.example.cube.common.storage;

import lombok.Getter;

@Getter
public class StoredFile {
    private final String externalId;
    private final long size;

    public StoredFile(String externalId, long size) {
        this.externalId = externalId;
        this.size = size;
    }
}