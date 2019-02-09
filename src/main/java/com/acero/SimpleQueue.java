package com.acero;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
public class SimpleQueue {

    public static void main(String[] args) {
        SpringApplication.run(SimpleQueue.class, args);
    }

    @RequestMapping("/health")
    public boolean health(){
        return true;
    }
}
