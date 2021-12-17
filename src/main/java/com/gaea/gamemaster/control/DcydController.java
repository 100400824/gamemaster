package com.gaea.gamemaster.control;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dcyd")
public class DcydController {


    @GetMapping("/get")
    public String getInfo(){

        return "runing...";
    }
}
