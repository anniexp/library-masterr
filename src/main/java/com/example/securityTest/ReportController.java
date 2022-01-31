/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.securityTest;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author Lenovo
 */
@Controller
public class ReportController {
    @Autowired
    ReportsRepository reportRepository;
BookService bookService;

    @GetMapping("/reports")
    public String index(Model model) {
        model.addAttribute("reports", reportRepository.findAll());
        return "reports";
    } 
     @GetMapping("/reports/new")
    public String showNewBookForm(Model model, @Valid Book book, String name, @Valid User user) {
       
         
        model.addAttribute("report", new Report());
        model.addAttribute("book", book);
        model.addAttribute("user", user);
        
        return "add-report";
    }
    
    @PostMapping("/reports/addreport")
    public String addAuthor(@Valid Book book, BindingResult result) {
        if (result.hasErrors()) {
            return "add-report";
        }
        
        bookService.save(book);
        return "redirect:/";
    }

    
}
