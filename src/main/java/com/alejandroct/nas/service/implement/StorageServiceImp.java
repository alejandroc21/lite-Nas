package com.alejandroct.nas.service.implement;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.alejandroct.nas.exception.DirectoryNotFoundException;
import com.alejandroct.nas.service.IStorageService;
import jakarta.annotation.PostConstruct;

@Service
public class StorageServiceImp implements IStorageService{
    
    String root = "src/main/resources/media";
    private Map<String, String> filesExtension;
    long maxSize=100*1024*1024;

    @PostConstruct
    public void init(){
        initFilesExtension();
        createDirectories();
    }

    @Override
    public List<String> listFolderFiles(String fileType){
        Path directory = Paths.get(root).resolve(fileType).normalize().toAbsolutePath();
        if (!Files.exists(directory)) {
            throw new DirectoryNotFoundException("Directory does not exist: "+fileType);
        }
        List<String> fileNames = new ArrayList<>();
        try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)){
            for(Path path : directoryStream){
                if(Files.isRegularFile(path)){
                    fileNames.add(path.getFileName().toString());
                }
            }
        }catch(IOException e){
            throw new RuntimeException("Could not list files.");
            
        }
        return fileNames;
    }

    @Override
    public String saveFile(MultipartFile file) {
        try {
            long fileSize = file.getSize();
        
            if(file.isEmpty()){
                throw new RuntimeException("The file is empty.");
            }
            if(fileSize>=maxSize){
                throw new RuntimeException("File size exceeds limit.");
            }

            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String filename = originalFilename;
            String fileType = getFileType(originalFilename);

            Path destinationDirectory = Paths.get(root).resolve(fileType).normalize().toAbsolutePath();
            Files.createDirectories(destinationDirectory);

            //checks if the file exists and appends an incremental number to filename
            int counter = 1;
            Path destinationFile = destinationDirectory.resolve(filename).normalize().toAbsolutePath();
            while(Files.exists(destinationFile)){
                int lastDotIndex = originalFilename.lastIndexOf(".");
                String filenameWithoutExtension = (lastDotIndex != -1) ? originalFilename.substring(0, lastDotIndex) : originalFilename;
                String fileExtension = (lastDotIndex != -1) ? originalFilename.substring(lastDotIndex) : "";
                filename = filenameWithoutExtension+"("+counter+")"+fileExtension;
                destinationFile = destinationDirectory.resolve(filename).normalize().toAbsolutePath();
                counter++;
            }

            try(InputStream inputStream = file.getInputStream()){
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        
            return filename;

        } catch (Exception e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }

    @Override
    public List<String> saveMultiFile(List<MultipartFile> files){
        List<String> filenames = new ArrayList<>();

        for(MultipartFile file: files){
            filenames.add(saveFile(file));
        }
        return filenames;
    }

    @Override
    public Resource loadFile(String filename) {
        try {
            String fileType = getFileType(filename);
            Path file = Paths.get(root+"/"+fileType).resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if(resource.exists() || resource.isReadable()){
                return resource;
            }else{
                throw new RuntimeException("Could not load file: "+filename);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load file.");
        }
    }

    private void initFilesExtension(){
        filesExtension = new HashMap<>();
        filesExtension.put("images", "jpg, jpeg, png, gif");
        filesExtension.put("music", "mp3, wav, m4a, mpeg");
        filesExtension.put("documents", "pdf, doc, docx, pptx, xlsx, txt");
        filesExtension.put("videos", "mp4, avi, mkv");
        filesExtension.put("other", "");
    }

    private void createDirectories(){
        //This is just a temporal solution, trust me
        try {
            Files.createDirectories(Paths.get(root).resolve("images").normalize().toAbsolutePath());
            Files.createDirectories(Paths.get(root).resolve("music").normalize().toAbsolutePath());
            Files.createDirectories(Paths.get(root).resolve("videos").normalize().toAbsolutePath());
            Files.createDirectories(Paths.get(root).resolve("documents").normalize().toAbsolutePath());
            Files.createDirectories(Paths.get(root).resolve("other").normalize().toAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create base directories.");
        }
    }

    private String getFileType(String filename){
        String extension = StringUtils.getFilenameExtension(filename);
        if (extension!=null) {
            for(Map.Entry<String, String> entry : filesExtension.entrySet()){
                if(entry.getValue().contains(extension.toLowerCase())){
                    return entry.getKey();
                }
            }
        }
        return "/other";
    }
    
}
