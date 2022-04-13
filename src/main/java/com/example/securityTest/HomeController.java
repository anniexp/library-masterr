/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.securityTest;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
@Controller
public class HomeController {
@Autowired
    private HttpServletRequest request;
 
    @GetMapping("index")
    public String viewHomePageAgain() {
          Principal currentUser = UserController.getCurrentUser(request);
                System.out.println("Current user is : " + currentUser);
        return "index";
    }

    @GetMapping("/home")
    public String viewHomePage3() {
        return "redirect:/index";
    }
   /* @GetMapping("/css/shards.min.css")
    public String viewHomePage4() {
        return "redirect:/index";
    }
    @GetMapping("/css/newcss.css")
    public String viewHomePage5() {
        return "redirect:/index";
    }*/
 @GetMapping("/newjsp")
    public String viewHomePage5() {
        return "newjsp";
    }
    
    @GetMapping("/contact-us")
    public String viewContactUsPage() {
        return "contact-us";
    }
    
}

