package com.example.securityTest;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author Lenovo
 */
@Controller
public class ReportController {
    
    @Autowired
    ReportRepository reportRepository;
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
    @Autowired
    ReportService reportService;
    
  
    
    @GetMapping("/reports")
    public String index(Model model) {
        List<Report> reports = null;
        if (reports != null) {
            //reports = bookService.findByKeyword(bookName);
        } else {
            reports = reportRepository.findAll();
        }
        model.addAttribute("reports", reports);
        //  model.addAttribute("reports", reportRepository.findAll());
        return "reports";
    }
    
    @GetMapping("/reports/new-report")
    public String showNewReportForm(Model model
    ) {
        
        List<Book> book = bookService.listAvailableBooks();
        List<User> users = userRepository.findAll();
        Principal currentEmployee = UserController.getCurrentUser(request);

        // User currentUserEmployee= (User) currentEmployee;
        //if it doesnt work, then to build it with a constructor, if that does not work, 
        //then firstly get the name of the cuurent user, then loop all users, if the
        //name equals, then this is the cuurent user 
        //User currentUserEmployee= new User(currentEmployee., String password,
        //String username, String rolee, boolean enabled);
        System.out.println("Number of books : " + book.size());
        System.out.println("Number of users : " + users.size());
        System.out.println("Current employeeee is : " + currentEmployee);
        
        model.addAttribute("book", book);
        model.addAttribute("users", users);
        model.addAttribute("report", new Report());
        
        return "add-report";
    }
    
    @PostMapping("/reports/add_report")
    public String addReport(@Valid Report rep, BindingResult result, Model model
    ) {
        //current date
        Date dateCreated = new Date();
        LocalDate currDate = LocalDate.now();

        //boolean isBookRented = bok.isIsRented();
        // System.out.println("Is the current book rented - " + bok.isIsRented());
        List<Report> reports = reportRepository.findAll();
        
        long reportLastId = reports.get(reports.size() - 1).getReportId();
        System.out.println("current last report id is: " + reportLastId);
        
        rep.setReportId(reportLastId + 1);
        System.out.print("report :  " + rep);
        
        rep.setDateCreated(dateCreated);
        System.out.println("current date :  " + dateCreated);
        /*
        //set date of return of the bookto be 14 days after the current date 
            LocalDate dateReturn =  LocalDate.now().plusDays(14);  
            Instant instant = dateReturn.atTime(LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault()).toInstant();
            Date dateBookReturn = Date.from(instant);
         */
        
        Date returnDate = reportService.createDateAfterDate(currDate, 14);
        System.out.println("return date :  " + returnDate);
        
        rep.setLastUpdated(reportService.createDateAfterDate(currDate, 14));
        System.out.println("return date :  " + returnDate);
        
        rep.setIsReturned(false);

        /*
        if (isBookRented == false){
        reportService.save(report);    
        return "redirect:/reports";
    }else {
        
        return "add-report";
        }*/
        //if there are validation errors or the 
        if (result.hasErrors()) {
            
            return "add-report";
        }
        Book bookToUpdate = rep.getBook();
        bookToUpdate.setIsRented(true);
        
        bookService.save(bookToUpdate);
       
        
        User us = rep.getBorrower();
      List<Book> usBorrwReq  = us.getBorrowRequests();
      
      List<Book> newBorrList = new ArrayList<>();
      for ( Book obj: usBorrwReq){
       if (obj.equals(bookToUpdate)){
         //  usBorrwReq.remove(obj);         
          newBorrList.add(obj);
          System.out.println(newBorrList.size());
       }
      }
       System.out.println(usBorrwReq.size());
      usBorrwReq.removeAll(newBorrList);
       System.out.println(usBorrwReq.size());
       
       us.setBorrowRequests(usBorrwReq);
      
      
       
       
       userRepository.save(us);
        reportService.save(rep);
        model.addAttribute("reports", reportRepository.findAll());
        
        return "redirect:/reports";
    }
    
     @GetMapping("/reports/edit/{reportId}")
    public String showUpdateForm(@PathVariable("reportId") long reportId, Model model) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid report Id:" + reportId));
        
        
        //create a list of all currently available plus the current book
         List<Book> book = bookService.listAvailableBooks();
                 System.out.println("Number of books : " + book.size());

         book.add(report.getBook());
                 System.out.println("Number of books : " + book.size());

                       System.out.println("Create date : " + report.getDateCreated());
                      System.out.println("Return date  : " + report.getLastUpdated());     
                      
 model.addAttribute("dateCreated", report.getDateCreated());
 model.addAttribute("dateReturned", report.getLastUpdated());
         
         
        List<User> users = userRepository.findAll();
        Principal currentEmployee = UserController.getCurrentUser(request);

        // User currentUserEmployee= (User) currentEmployee;
        //if it doesnt work, then to build it with a constructor, if that does not work, 
        //then firstly get the name of the cuurent user, then loop all users, if the
        //name equals, then this is the cuurent user 
        //User currentUserEmployee= new User(currentEmployee., String password,
        //String username, String rolee, boolean enabled);
      
       
        System.out.println("Number of users : " + users.size());
        System.out.println("Current employeeee is : " + currentEmployee);
        
        model.addAttribute("book", book);
        model.addAttribute("users", users);
        model.addAttribute("report", report);
     
        return "update-report";
    }

    @PostMapping("/reports/update/{reportId}")
    public String updateBook(@PathVariable("reportId") long reportId, @Valid Report report,
            BindingResult result, Model model
           ) {
        

        if (result.hasErrors()) {
           System.out.println("Id of report to be edited : " + report.getReportId());
            report.setReportId(report.getReportId());
                       System.out.println("set Id  : " +report.getReportId());

            return "update-report";
        }
      
    
   Book bookToUpdate = report.getBook();
        bookToUpdate.setIsRented(false);
        
        bookService.save(bookToUpdate);
        reportService.save(report);
        
        model.addAttribute("reports", reportRepository.findAll());
        return "redirect:/reports";
    }

    @GetMapping("/reports/delete/{reportId}")
    public String deleteBook(@PathVariable("reportId") long reportId, Model model) {
        
       Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid report Id:" + reportId));
       
        Book bookToUpdate = report.getBook();
        bookToUpdate.setIsRented(false);
        
        bookService.save(bookToUpdate);
        reportRepository.delete(report);
        
        model.addAttribute("reports", reportRepository.findAll());
        return "redirect:/reports";
    }
    
    
    /*
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
     */
}
