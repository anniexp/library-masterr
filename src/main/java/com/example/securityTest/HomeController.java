/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.securityTest;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
 @Autowired
    BookRepository bookRepository;

    @Autowired
    BookService bookService;
    
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
    
     @GetMapping("/")
    public String loadHttpResponse404CustomPage() {
        return "redirect:/index";
    }
    
    @GetMapping("/genres")
    public String loadGenresPage(Model model) {
        
        List<String> genresList = new ArrayList<>();
         List<String> firstList = new ArrayList<>();
        List<String> secondList = new ArrayList<>();
        List<String> thirdList = new ArrayList<>();
        
        for (Genres genre : Genres.values()) { 
            
          String gen = genre.toString();
            genresList.add(gen);
            System.out.println("Numbers of elements: " +genresList.size() );
        }
        
        
            bookService.splitStringListIntoSeveralLists (genresList,firstList,secondList,thirdList);
        
            System.out.println("Numbers of elements in first sub list: " +firstList.size() );
            System.out.println("Numbers of elements in second: " +secondList.size() );
            System.out.println("Numbers of elements in third: " +thirdList.size() );

          model.addAttribute("firstList", firstList); 
          model.addAttribute("secondList", secondList); 
          model.addAttribute("thirdList", thirdList); 
        
        
        
        return "genres";
    }
    
     @GetMapping("/genres/genreName")
    public String loadGenresPage(Model model,
            @PathVariable("genreName") String genre) {
        
    
        List<Book> books = bookService.findByGenres(genre);
 
        model.addAttribute("books", books); 
        
        return "books";
    }
    
    
    
}

