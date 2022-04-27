/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.securityTest;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @Autowired
    HomeService homeService;
    
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
        
       /* for (Genres genre : Genres.values()) { 
            
          String gen = genre.toString();
            genresList.add(gen);
            System.out.println("Numbers of elements: " +genresList.size() );
        }
        System.out.println(genresList);
                   
                    genresList.sort(String.CASE_INSENSITIVE_ORDER);  //sorts the list in case insensitive order
                            System.out.println(genresList);

                    genresList.sort(Comparator.naturalOrder());    //sorts list in ascending order  

        System.out.println(genresList);*/
       
           bookService.createGenresList( genresList);


            bookService.splitStringListIntoSeveralLists (genresList,firstList,secondList,thirdList);
        
            System.out.println("Numbers of elements in first sub list: " +firstList.size() );
            System.out.println("Numbers of elements in second: " +secondList.size() );
            System.out.println("Numbers of elements in third: " +thirdList.size() );

          model.addAttribute("genresList", genresList); 
          model.addAttribute("secondList", secondList); 
          model.addAttribute("thirdList", thirdList); 
                  model.addAttribute("firstList", firstList); 

        
        
        return "genres";
    }
    
        
    
    
}

