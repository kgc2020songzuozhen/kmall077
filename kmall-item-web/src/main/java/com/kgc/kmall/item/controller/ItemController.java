package com.kgc.kmall.item.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ItemController {
    @RequestMapping("/test")
    @ResponseBody
    public String test(){
        return "index";
    }
}
