package com.example.demo;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HelloWorld {
    @RequestMapping("/")
    public @ResponseBody String index() throws Exception {
        return "Hello World";
    }
 
    @RequestMapping("/demo")
    public @ResponseBody String demo() throws Exception {
        return "데모 페이지에 접속 하셨습니다.";
    }
    
    @RequestMapping("/jsp")
    public String jsp() throws Exception {
        return "index";
    }
    
    @RequestMapping("/react")
    public String react() throws Exception {
        return "react";
    }
    
    @RequestMapping("/mav")
    public ModelAndView mav() throws Exception {
        ModelAndView mav = new ModelAndView("mavSample");
        
        mav.addObject("key", "fruits");
        
        ArrayList<String> fruitList = new ArrayList<String>();
        
        fruitList.add("apple");
        fruitList.add("orange");
        fruitList.add("banana");
         
        mav.addObject("value", fruitList);
        
        return mav;
    }
}