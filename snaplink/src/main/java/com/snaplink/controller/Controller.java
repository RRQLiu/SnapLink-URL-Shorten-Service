package com.snaplink;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    
    @GetMapping("/")
    public String home() {
        return "Welcome to Snaplink Application!";
    }
    
    @GetMapping("/hello")
    public String hello() {
        return "Hello from Snaplink!";
    }
}