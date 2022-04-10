/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.securityTest;

import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author Lenovo
 */
@Controller
public class ReportController {
    @Autowired
    ReportsRepository reportRepository;
            @Autowired
BookService bookService;
              @Autowired
    BookRepository bookRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private HttpServletRequest request;
     

    @GetMapping("/reports")
    public String index(Model model) {
                List<Report> reports = null;
 if (reports != null) {
            //reports = bookService.findByKeyword(bookName);
        }
        else
        {
            reports = reportRepository.findAll();
        }
        model.addAttribute("reports", reports);
      //  model.addAttribute("reports", reportRepository.findAll());
        return "reports";
    } 
   /* 
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
    }*/

    @GetMapping("/books/getBook/{bookId}")
    public String showBorrowBookForm(@PathVariable("bookId") long bookId, Model model) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + bookId));
        boolean isBookRented = book.isIsRented();
        System.out.println(isBookRented);
        if (isBookRented == false) {

            model.addAttribute("book", book);
            model.addAttribute("report", new Report());

            return "borrow-book";
        }

        return "redirect:/books";

    }

    @PostMapping("/books/borrowBook/{bookId}")
    public String borrowBook(@PathVariable("bookId") long bookId, @Valid Book book,
            BindingResult result, Model model, Report report) {

        System.out.println("Is the current book rented - " + book.isIsRented());

        if (!book.isIsRented()) {
            book.setIsRented(true);
            bookRepository.save(book);

            Principal currentUser = UserController.getCurrentUser(request);
            // List<User> users = userRepository.findAll();
            User borrower = (User) currentUser;
             System.out.println("Current user has name : " + borrower.getUsername());
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date dateCreated = new Date();
            dateFormat.format(dateCreated);

            System.out.println("Current date is " + dateCreated);
            report.setBook(book);
            report.setBorrower(borrower);
            report.setDateCreated(dateCreated);
            report.setLastUpdated(dateCreated);

            reportRepository.save(report);

            model.addAttribute("books", bookRepository.findAll());

        }

        return "redirect:/books";

    }
    
}
