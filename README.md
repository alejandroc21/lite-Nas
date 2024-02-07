
# Lite-NAS

This project is a basic implementation of network attached storage, facilitating the sharing of files between different devices.

## How to configure
* We must change the maximum size allowed form `application.properties` which by default will be 100MB.
```
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB
```
**Warning:** sending a file larger than allowed will lead to an exception.

* We can choose the folders that are created and the type of data that each one will admit from `StorageServiceImp.java`.
By default we find these, the undefined extension files will be saved in the "other" directory.

```java
private void initFilesExtension() {
        filesExtension = new HashMap<>();
        filesExtension.put("images", "jpg, jpeg, png, gif");
        filesExtension.put("music", "mp3, wav, m4a, mpeg");
        filesExtension.put("documents", "pdf, doc, docx, pptx, xlsx, txt");
        filesExtension.put("videos", "mp4, avi, mkv");
        filesExtension.put("other", "");
    }
```
## How to use

Just run Spring Boot application.
You can find a graphical interface at `localhost:8080`, although the use of endpoints makes the application independent of it.


**Warning:** This project was developed in Linux so any errors coming from the locations could be due to the `"/" pathSeparator.`

### Technologies
* Spring Boot 3.2.1
* JavaScript
* HTML
* CSS
