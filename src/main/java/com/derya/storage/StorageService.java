package com.derya.storage;

import com.derya.domain.FileMetadata;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StorageService {

    String store(MultipartFile file, String userName, String type);

    Resource loadAsResource(String filename);

    List<String> listUserFiles(String userName);

    FileMetadata readMetadata(String username, String filename);

    FileMetadata modifyMetadataType(String username, String filename, String type);
}