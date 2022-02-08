/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.securityTest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Lenovo
 *
 *
 */
@RestController
public class HomeController {

  /*  @RequestMapping("/")
    public String viewHomePage() {
        return "redirect:/";
    }

    @GetMapping("/index")
    public String viewHomePageAgain() {
        return "redirect:/";
    }

    @GetMapping("/home")
    public String viewHomePage3() {
        return "redirect:/";
    }*/
    @GetMapping("/css/newcss.css")
    public String viewHomePage4() {
        return "redirect:/";
    }

}
