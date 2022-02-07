/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.securityTest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String index(Model model, @RequestParam(name = "searchAuthor", required = false) String authorName) {
        List<Author> authors = null;
         if (authorName != null) {
            authors = authorRepository.findByKeyword(authorName);
        } else {
            authors = authorRepository.findAll();
        }
        model.addAttribute("authors", authors);
        return "authors";
    }

    @GetMapping("/authors/new")
    public String showNewBookForm(Model model) {
        List<Book> books = bookRepository.findAll();
        model.addAttribute("author", new Author());
        return "add-author";
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
        Author author = authorService.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid author Id:" + authorId));

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
