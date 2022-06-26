package com.derya.domain;

public class FileMetadata {
    private final String name;
    private final String type;

    public FileMetadata(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }


}
