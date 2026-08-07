package com.example.cube.common.storage;

/**
 * Abstraction over wherever attachment bytes actually live. Today the only
 * implementation is OneDrive (see .onedrive package), but any future module
 * needing file storage depends on this interface, not on Graph API directly.
 */
public interface FileStorageService {

    StoredFile upload(String folderPath, String fileName, String contentType, byte[] content);

    byte[] download(String externalFileId);
}