package com.alejandroct.nas.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import org.springframework.core.io.Resource;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DataFile {
    private String name;
    private String url;
    private Resource logo;
    private Date creationDate;
    private long bytesSize;
    @JsonIgnore
    private Path path;

    public DataFile(){}

    public DataFile(Path path){
        this.path = path;
        this.name = getFileName();
        this.creationDate = getcreationDate();
        this.bytesSize = getBytesSize();
    }

    private String getFileName(){
        return path.getFileName().toString();
    }

    private Date getcreationDate(){
        try {
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            return new Date(attributes.creationTime().toMillis());
        } catch (IOException e) {
            return null;
        }
    }

    private long getBytesSize(){
        try {
            return Files.size(path);
        } catch (IOException e) {
            return 0;
        }
    }
}
