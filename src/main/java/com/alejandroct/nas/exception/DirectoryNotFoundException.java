package com.alejandroct.nas.exception;

public class DirectoryNotFoundException extends RuntimeException{
    
    public DirectoryNotFoundException(String message){
        super(message);
    }
}
