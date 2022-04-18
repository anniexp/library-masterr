/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.securityTest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Lenovo
 */
@Controller
public class AuthorController {

    @Autowired
    AuthorRepository authorRepository;
    @Autowired
    BookRepository bookRepository;

    @Autowired
    private AuthorService authorService;

    @GetMapping("/authors")
    public String index(Model model, @RequestParam(name = "searchAuthor", required = false) String authorName,
               @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        
        List<Author> authors = null;
         Pageable paging = (Pageable) PageRequest.of(page, size);  
         Page<Author> pageTuts;
        
         if (authorName != null) {
               pageTuts = authorRepository.findByKeyword(authorName, paging);
           // authors = authorRepository.findByKeyword(authorName);
        } else {
            pageTuts = authorRepository.findAll(paging);
        }
         
          authors = pageTuts.getContent();     
        model.addAttribute("currentPage", pageTuts.getNumber());
        System.out.println("current page number is: " + pageTuts.getNumber());
        System.out.println("number of elements: " + pageTuts.getTotalElements());
        model.addAttribute("totalItems", pageTuts.getTotalElements());
         System.out.println("number of pages: " + pageTuts.getTotalPages());
        model.addAttribute("totalPages", pageTuts.getTotalPages());
        
        model.addAttribute("authors", authors);
        return "authors";
    }

    @GetMapping("/authors/new")
    public String showNewBookForm(Model model) {
        List<Book> books = bookRepository.findAll();
        model.addAttribute("author", new Author());
        return "add-author";
    }
    
      @GetMapping("/authors/author-details/{authorId}")
    public String showBookDetails(@PathVariable("authorId") long authorId, Model model,
     @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        List<Book> books = null;
        Author author = authorService.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid author Id:" + authorId));
   
         if (author == null) {   
                      System.out.println("author does not exist");

          return "page404";
             }  
         else
         {
         Pageable paging = (Pageable) PageRequest.of(page, size);  
         Page<Book> pageTuts;
         pageTuts = bookRepository.findByAuthor(author, paging);
         System.out.println("number of books by this author: " + author.getBooks().size());

         
         
          books = pageTuts.getContent();     
        model.addAttribute("currentPage", pageTuts.getNumber());
        System.out.println("current page number is: " + pageTuts.getNumber());
        System.out.println("number of elements: " + pageTuts.getTotalElements());
        model.addAttribute("totalItems", pageTuts.getTotalElements());
         System.out.println("number of pages: " + pageTuts.getTotalPages());
        model.addAttribute("totalPages", pageTuts.getTotalPages());
        
        
        model.addAttribute("author", author);
        model.addAttribute("books",  books);
        return "authors-details";
         }
    }

    @PostMapping("/authors/addauthor")
    public String addAuthor(@Valid Author author, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-author";
        }

        authorService.save(author);
        model.addAttribute("authors", authorRepository.findAll());

        return "redirect:/authors";
    }

    @GetMapping("/authors/edit/{authorId}")
    public String showAuthorUpdateForm(@PathVariable("authorId") long authorId, Model model) {
        List<Book> books = bookRepository.findAll();
        Optional<Author> author = authorService.findById(authorId);           
             if (author == null) {   
          return "page404";
             }
        

        model.addAttribute("author", author);
        model.addAttribute("books", books);
        return "update-author";
    }

    @PostMapping("/authors/update/{authorId}")
    public String updateAuthor(@PathVariable("authorId") long id, @Valid Author author,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            author.setAuthorId(id);
            return "authors/update-author";
        }

        authorService.save(author);
        model.addAttribute("author", authorRepository.findAll());
        return "redirect:/authors";
    }

    @GetMapping("/authors/delete/{authorId}")
    public String deleteAuthor(@PathVariable("authorId") long id, Model model) {
        Author author = authorService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));
        authorService.delete(author);
        return "redirect:/authors";
    }

  

}
