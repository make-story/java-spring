package com.god.bo.test.controller; 

import org.springframework.stereotype.Controller; 
import org.springframework.web.bind.annotation.RequestMapping; 
import org.springframework.web.bind.annotation.ResponseBody;


@Controller 
public class TestController { 
	@RequestMapping(value = "/home") 
	public String home() { 
		return "index.html"; 
	} 
	
	@ResponseBody 
	@RequestMapping("/valueTest") 
	public String valueTest() { 
		String value = "테스트 String"; 
		return value; 
	}
	
	@RequestMapping("/thymeleafTest") 
	public String thymeleafTest(/*Model model*/) { 
		//TestVo testModel = new TestVo("goddaehee", "갓대희") ; 
		//model.addAttribute("testModel", testModel); 
		return "thymeleaf/thymeleafTest"; 
	}
}

