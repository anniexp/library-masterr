package com.example.securityTest;

import static com.sun.corba.se.spi.presentation.rmi.StubAdapter.request;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class BookController {
//zadeistva proccess if depebdanty injection

    @Autowired
    BookRepository bookRepository;

    @Autowired
    BookService bookService;

    @Autowired
    ReportsRepository reportRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private HttpServletRequest request;
     
    
    static ArrayList<Author> authors;

   /* public BookController(BookRepository bookRepository, BookService bookService, ReportsRepository reportRepository, AuthorRepository authorRepository, UserRepository userRepository, HttpServletRequest request, ArrayList<Author> authors) {
        this.bookRepository = bookRepository;
        this.bookService = bookService;
        this.reportRepository = reportRepository;
        this.authorRepository = authorRepository;
        this.userRepository = userRepository;
        this.request = request;
        this.authors = authors;
    }
*/
    


    @GetMapping("/books")
    public String books(Model model, @RequestParam(name = "searchBook", required = false) String bookName) {
        List<Book> books = null;
        if (bookName != null) {
            books = bookService.findByKeyword(bookName);
        } else {
            books = bookRepository.findAll();
        }
        model.addAttribute("books", books);
        return "books";
    }

    @GetMapping("/books/signup")
   // @ResponseBody
    public String showSignUpForm(Model model, @ModelAttribute("authors") ArrayList<Author> authors) {
      authors.addAll(authorRepository.findAll());

        model.addAttribute("book", new Book());
        model.addAttribute("authors", authors);
        model.addAttribute("report", new Report());

        return "add-book";
    }

    
    private Map<Long,Author> authorMap = new HashMap<>();
        private Map<Long,Report> reportMap = new HashMap<>();

            
    @PostMapping("/books/addbook")
    public String addBook(@Valid Book book, BindingResult result, Model model,
            @ModelAttribute("authors") ArrayList<Author> authors, RedirectAttributes attributes ) {
        System.out.print("book " + book);
        
        if (authors.isEmpty()){
        authors.addAll(authorRepository.findAll());
        model.addAttribute("authors", authors);
        }

        List<Book> books = bookRepository.findAll();

        long bookLastId = books.get(books.size() - 1).getBookId();
        System.out.println("current last book id is: " + bookLastId);

        book.setBookId(bookLastId + 1);
        System.out.print("book " + book);

        boolean dublicateIsbn = bookService.checkIfIsbnDublicate(book.getIsbn());

        if (result.hasErrors()||dublicateIsbn==true) {
            
            
            return "add-book";
        }

        bookRepository.save(book);

        //  bookService.save(book);
        model.addAttribute("books", bookRepository.findAll());

        return  "redirect:/books";

    }

    @GetMapping("/books/edit/{bookId}")
    public String showUpdateForm(@PathVariable("bookId") long bookId, Model model) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + bookId));
        List<Author> authors = authorRepository.findAll();
        model.addAttribute("bok", book);
        model.addAttribute("authors", authors);
        return "update-book";
    }

    @PostMapping("/books/update/{bookId}")
    public String updateBook(@PathVariable("bookId") long bookId, @Valid Book book,
            BindingResult result, Model model) {
        

        if (result.hasErrors()) {
           System.out.println("Id of boook to be edited : " + book.getBookId());
            book.setBookId(book.getBookId());
            return "update-book";
        }
        //else if(book.getBookId()==result.)
        if ( bookService.checkIfIsbnDublicateEdit(book.getIsbn(), bookId)==true){
            
            
            book.setBookId(bookId);
                    return "redirect:/books/edit/{bookId}";

        }

        bookRepository.save(book);
        model.addAttribute("books", bookRepository.findAll());
        return "redirect:/books";
    }

    @GetMapping("/books/delete/{bookId}")
    public String deleteBook(@PathVariable("bookId") long bookId, Model model) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + bookId));
        bookRepository.delete(book);
        model.addAttribute("books", bookRepository.findAll());
        return "redirect:/books";
    }

    @GetMapping("/books/search")
    public String search(@RequestParam(name = "search", required = false) String title, Model model) {
        model.addAttribute("book", bookRepository.findByTitleStartingWith(title));
        return "redirect:/books";
    }

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
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date dateCreated = new Date();
            dateFormat.format(dateCreated);

            System.out.println("Current date is " + dateCreated);
            report.setBook(book);
            report.setBorrower(borrower.getUsername());
            report.setDateCreated(dateCreated);
            report.setLastUpdated(dateCreated);

            reportRepository.save(report);

            model.addAttribute("books", bookRepository.findAll());

        }

        return "redirect:/books";

    }
    
    
    /*
    
     @GetMapping("/books/signup")
    public String showSignUpForm(Model model) {
        List<Author> authors = authorRepository.findAll();

        model.addAttribute("book", new Book());

        model.addAttribute("authors", authors);
        model.addAttribute("report", new Report());

        return "add-book";
    }

    @PostMapping("/books/addbook")
    public String addBook(@Valid Book book, BindingResult result, Model model) {
        System.out.print("book " + book);

        List<Book> books = bookRepository.findAll();

        long bookLastId = books.get(books.size() - 1).getBookId();
        System.out.println("current last book id is: " + bookLastId);

        book.setBookId(bookLastId + 1);
        System.out.print("book " + book);

        boolean dublicateIsbn = bookService.checkIfIsbnExists(book.getIsbn());

        if (result.hasErrors()||dublicateIsbn==true) {
            return "add-book";
        }

        bookRepository.save(book);

        //  bookService.save(book);
        model.addAttribute("books", bookRepository.findAll());

        return "redirect:/books";

    }
    
     @PostMapping("/books/update/{bookId}")
    public String updateBook(@PathVariable("bookId") long bookId, @Valid Book book,
            BindingResult result, Model model) {
        

        if (result.hasErrors()) {
            book.setBookId(bookId);
            return "update-book";
        }
        

        bookRepository.save(book);
        model.addAttribute("books", bookRepository.findAll());
        return "redirect:/books";
    }

*/
    
}
