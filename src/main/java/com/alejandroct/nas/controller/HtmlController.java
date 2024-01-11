package com.alejandroct.nas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HtmlController {
    
    @GetMapping("/")
    public String index(){
        return "index.html";
    }
}
