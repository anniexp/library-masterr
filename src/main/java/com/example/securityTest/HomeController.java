package com.example.securityTest;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Lenovo
 */
@Controller
public class HomeController {
    private final HttpServletRequest request;
    private final BookService bookService;

    @Autowired
    public HomeController(HttpServletRequest request, BookService bookService) {
        this.request = request;
        this.bookService = bookService;
    }

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

        bookService.createGenresList(genresList);
        bookService.splitStringListIntoSeveralLists(genresList, firstList, secondList, thirdList);

        model.addAttribute("genresList", genresList);
        model.addAttribute("secondList", secondList);
        model.addAttribute("thirdList", thirdList);
        model.addAttribute("firstList", firstList);

        return "genres";
    }
}

