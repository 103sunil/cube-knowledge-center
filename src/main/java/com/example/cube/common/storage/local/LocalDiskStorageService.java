package com.example.cube.common.storage.local;

import com.example.cube.common.exception.FileStorageException;
import com.example.cube.common.storage.FileStorageService;
import com.example.cube.common.storage.StoredFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Writes attachment bytes to local disk instead of OneDrive. Active only
 * under the "local" Spring profile (spring.profiles.active=local) so
 * attachment upload/list/download can be exercised end-to-end through
 * Postman before real Azure AD credentials exist. Never use this profile
 * in staging/production - files here are not backed up, not shared across
 * WebLogic nodes, and vanish if someone clears the directory.
 */
@Service
@Profile("local")
public class LocalDiskStorageService implements FileStorageService {

    @Value("${storage.local.base-dir:./local-storage}")
    private String baseDir;

    @Override
    public StoredFile upload(String folderPath, String fileName, String contentType, byte[] content) {
        try {
            Path dir = Paths.get(baseDir, folderPath);
            Files.createDirectories(dir);

            String storedName = UUID.randomUUID() + "_" + fileName;
            Path filePath = dir.resolve(storedName);
            Files.write(filePath, content);

            return new StoredFile(filePath.toAbsolutePath().toString(), content.length);
        } catch (IOException e) {
            throw new FileStorageException("Failed to write file to local storage: " + e.getMessage());
        }
    }

    @Override
    public byte[] download(String externalFileId) {
        try {
            return Files.readAllBytes(Paths.get(externalFileId));
        } catch (IOException e) {
            throw new FileStorageException("Failed to read file from local storage: " + e.getMessage());
        }
    }
}