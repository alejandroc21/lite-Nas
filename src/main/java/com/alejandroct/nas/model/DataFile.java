package com.alejandroct.nas.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DataFile {
    private String name;
    private String url;
    private String logo;
    private Date creationDate;
    private long bytesSize;
    @JsonIgnore
    private Path path;
    @JsonIgnore
    String host = "http://127.0.0.1:8080";

    public DataFile(){}

    public DataFile(Path path){
        this.path = path;
        this.name = getFileName();
        this.url = getFileUrl();
        this.logo = getLogoIcon();
        this.creationDate = getcreationDate();
        this.bytesSize = getBytes();
    }

    private String getFileName(){
        return path.getFileName().toString();
    }

    private String getFileUrl(){
        String fileName = path.getFileName().toString();
        String url = ServletUriComponentsBuilder.fromHttpUrl(host).path("/media/").path(fileName).toUriString();
        return url;
    }

    private String getLogoIcon(){
        //String iconDirectory = "static/image/";
        String pathFile = path.toString();
        String iconName= "";
        if(pathFile.contains("images/")){
            iconName+="icon_image.png";            
        }     
        if(pathFile.contains("documents/")){
            iconName+="icon_doc.png";            
        }
        if(pathFile.contains("music/")){
            iconName+="icon_music.png";            
        }
        if(pathFile.contains("videos/")){
            iconName+="icon_video.png";            
        }
        if(pathFile.contains("other/")){
            iconName+="icon_other.png";            
        }   
        
        String logoUrl = ServletUriComponentsBuilder.fromHttpUrl(host).path("/media/").path(iconName).toUriString();
        return logoUrl;
    }

    private Date getcreationDate(){
        try {
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            return new Date(attributes.creationTime().toMillis());
        } catch (IOException e) {
            return null;
        }
    }

    private long getBytes(){
        try {
            return Files.size(path);
        } catch (IOException e) {
            return 0;
        }
    }

    public String getName(){
        return name;
    }

    public String getUrl(){
        return url;
    }

    public String getLogo(){
        return logo;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public long getBytesSize(){
        return bytesSize;
    }
}
