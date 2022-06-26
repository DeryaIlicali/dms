package com.derya.storage;

import com.derya.domain.FileMetadata;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    String store(MultipartFile file, String userName, String type);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    List<String> listUserFiles(String userName);

    void deleteAll();

    FileMetadata readMetadata(String username, String filename);

    FileMetadata modifyMetadataType(String username, String filename, String type);

}