package com.chhatkh.dev.services;

import com.chhatkh.dev.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path root;

    public FileStorageService(@Value("${app.upload-dir:uploads}") String dir) throws IOException {
        this.root = Paths.get(dir).toAbsolutePath().normalize();
        Files.createDirectories(root);
    }

    public String save(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new BadRequestException("File is empty");
        String type = file.getContentType();
        if (type == null || !type.startsWith("image/")) throw new BadRequestException("Only image files are allowed");
        String name = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        String ext = name.contains(".") ? name.substring(name.lastIndexOf('.') + 1) : "jpg";
        if (!ext.matches("[a-z0-9]{1,5}")) ext = "jpg";
        String stored = UUID.randomUUID() + "." + ext;
        try { Files.copy(file.getInputStream(), root.resolve(stored)); }
        catch (IOException e) { throw new BadRequestException("Could not store file"); }
        return "/uploads/" + stored;
    }
}
