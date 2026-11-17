package com.chhatkh.dev.services.impl;

import com.chhatkh.dev.exception.BadRequestException;
import com.chhatkh.dev.services.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    private final Path root;

    public FileStorageServiceImpl(@Value("${app.upload-dir:uploads}") String dir) throws IOException {
        this.root = Paths.get(dir).toAbsolutePath().normalize();
        Files.createDirectories(root);
    }

    @Override
    public String save(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new BadRequestException("File is empty");
        String name = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        String ext = name.contains(".") ? name.substring(name.lastIndexOf('.') + 1) : "jpg";
        if (!ext.matches("[a-z0-9]{1,5}")) ext = "jpg";
        String stored = UUID.randomUUID() + "." + ext;
        try {
            Files.copy(file.getInputStream(), root.resolve(stored), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BadRequestException("Could not store file");
        }
        return "/uploads/" + stored;
    }
}
