package com.example.cube.modules.knowledge.service;

import lombok.Getter;

/** Not a DTO - never serialized to JSON directly, used internally to carry
 *  raw bytes + metadata from AttachmentService to the controller so it can
 *  build the download response with the right headers. */
@Getter
public class DownloadedFile {
    private final String fileName;
    private final String contentType;
    private final byte[] content;

    public DownloadedFile(String fileName, String contentType, byte[] content) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.content = content;
    }
}