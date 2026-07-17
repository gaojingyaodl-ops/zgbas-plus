package com.spt.bas.web.controller.common;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/quick/search")
public class FastQueryController {

    @RequestMapping(value = "content")
    public String content(Model model) {
        
        
        return "quick-search";
    }
    
}
