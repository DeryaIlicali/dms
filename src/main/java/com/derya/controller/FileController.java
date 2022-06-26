package com.derya.controller;

import com.derya.commons.FileResponse;
import com.derya.domain.FileMetadata;
import com.derya.storage.StorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class FileController {

    private StorageService storageService;

    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }


    @GetMapping("/download/{username}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable("filename") String filename) {

        Resource resource = storageService.loadAsResource(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping(value = "/{username}/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public FileResponse uploadFile(@PathVariable("username") String userName,
                                   @RequestParam("fileType") String fileType,
                                   @RequestParam("file") MultipartFile file) {
        String name = storageService.store(file, userName, fileType);

        String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/download/")
                .path(name)
                .path(fileType)
                .toUriString();

        return new FileResponse(name, fileType, uri, file.getContentType(), file.getSize());
    }

    @PostMapping(value = "/{username}/upload-multiple-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public List<FileResponse> uploadMultipleFiles(@PathVariable("username") String userName,
                                                  @PathVariable("fileType") String fileType,
                                                  @RequestParam("files") MultipartFile[] files) {
        return Arrays.stream(files)
                .map(file -> uploadFile(userName, fileType, file))
                .collect(Collectors.toList());
    }

    @GetMapping("/{username}/list-all")
    @ResponseBody
    public ResponseEntity<List<String>> listAllFilesOfAUser(@PathVariable("username") String username) {

        List<String> fileList = storageService.listUserFiles(username);

        return ResponseEntity.ok()
                .body(fileList);
    }

    @GetMapping("/{username}/{filename}")
    @ResponseBody
    public ResponseEntity<FileMetadata> getMetadataOfAFile(@PathVariable("username") String username,
                                                           @PathVariable("filename") String filename) {

        FileMetadata fileMetadata = storageService.readMetadata(username, filename);

        return ResponseEntity.ok()
                .body(fileMetadata);
    }

    @PutMapping("/{username}/{filename}/{fileType}/modify")
    @ResponseBody
    public ResponseEntity<String> modifyType(@PathVariable("username") String username,
                                             @PathVariable("filename") String filename,
                                             @PathVariable("fileType") String filetype) {

        FileMetadata fileMetadata = storageService.modifyMetadataType(username, filename, filetype);

        return ResponseEntity.ok()
                .body("Type is updated");
    }
}
