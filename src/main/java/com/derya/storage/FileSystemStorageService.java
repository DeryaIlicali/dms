package com.derya.storage;

import com.derya.domain.FileMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;
    HashMap<Path, FileMetadata> fileMetadata = new HashMap<>();


    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage location", e);
        }
    }

    @Override
    public String store(MultipartFile file, String userName, String fileType) {
//        String filename = StringUtils.cleanPath(file.getOriginalFilename());

        String filename = file.getOriginalFilename();
        try {
            Path userPath = this.rootLocation.resolve(userName);
            File userFolder = userPath.toFile();
            if (!userFolder.exists()) {
                boolean isUserFolderCreated = userFolder.mkdirs();
                if (!isUserFolderCreated) {
                    throw new StorageException("Failed to create user folder");
                }
            }
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, userPath.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);
            }

            Path filePath = userPath.resolve(filename);
            // populate file metadata
            fileMetadata.put(filePath, new FileMetadata(filename, fileType));

        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }

        return filename;
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }


    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path filePath = load(filename);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new FileNotFoundException(
                        "Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new FileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public List<String> listUserFiles(String userName) {
        Path filePath = load(userName);
        File userFolder = filePath.toFile();
        if(userFolder.isDirectory()) {
             String[] userDocList = userFolder.list();
             if(userDocList != null){
                 return Arrays.asList(userDocList);
             }
        }
        throw new StorageException("");
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    public FileMetadata readMetadata(String username, String filename){
        // creating a object of Path class
        Path filePath = rootLocation.resolve(username).resolve(filename);
        return fileMetadata.get(filePath);
    }

    public FileMetadata modifyMetadataType(String username, String filename, String type) {
        Path filePath = rootLocation.resolve(username).resolve(filename);
        return fileMetadata.put(filePath, new FileMetadata(filename, type));
    }

}
