package com.derya.storage;

import com.derya.domain.FileMetadata;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

public class FileSystemStorageServiceTest {

    String testUploadRoot = "./test_uploads";
    StorageProperties storageProperties;
    FileSystemStorageService storage;
    MockMultipartFile file;
    String username;
    String fileType;

    @Before
    public void setUp() throws Exception {
        storageProperties = new StorageProperties();
        storageProperties.setLocation(testUploadRoot);
        storage = new FileSystemStorageService(storageProperties);
        file = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes());

        username = "TestUser";
        fileType = "Text";
    }

    @After
    public void tearDown() throws Exception {
        File storedFile = Paths.get(testUploadRoot).toFile();
        storedFile.delete();
    }

    @Test
    public void isFileUploaded() {
        storage.store(file, username, fileType);
        File storedFile = Paths.get(testUploadRoot).resolve(username).resolve("hello.txt").toFile();
        Assert.assertTrue(storedFile.exists());
    }

    @Test
    public void isUserFilesListed() {
        MockMultipartFile testFile = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes());

        storage.store(file, username, fileType);
        storage.store(testFile, username, fileType);
        List<String> userFiles = storage.listUserFiles(username);

        // Ensure file names in expected order.
        userFiles.sort(Comparator.naturalOrder());

        Assert.assertEquals(userFiles.get(0), file.getOriginalFilename());
        Assert.assertEquals(userFiles.get(1), testFile.getOriginalFilename());

    }

    @Test
    public void isFileMetadataTypeModified() throws InterruptedException {
        storage.store(file, username, fileType);
        FileMetadata fileMetadataBeforeUpdate = storage.readMetadata(username, file.getOriginalFilename());

        String updatedType = "Passport";

        storage.modifyMetadataType(username, file.getOriginalFilename(), updatedType);
        FileMetadata fileMetadataAfterUpdate = storage.readMetadata(username, file.getOriginalFilename());

        Assert.assertEquals(updatedType, fileMetadataAfterUpdate.getType());
        Assert.assertNotEquals(fileMetadataBeforeUpdate.getType(), fileMetadataAfterUpdate.getType());

    }
}