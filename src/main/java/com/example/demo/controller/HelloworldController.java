package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by huxm on 2019/8/20
 */
@RestController
public class HelloworldController {
    @RequestMapping("/hello")
    public String hello() {
        return "Hello World!";
    }


    @RequestMapping("/hello1")
    public String hello2() {
        return "Hello World!";
    }

    @RequestMapping("/hello2")
    public String hello3() {
        return "Hello World!";
    }
}
