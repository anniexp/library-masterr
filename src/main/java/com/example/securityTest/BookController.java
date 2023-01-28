package com.example.securityTest;

import java.io.IOException;

import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

@Controller
public class BookController {
    private final BookRepository bookRepository;
    private final BookService bookService;
    private final AuthorRepository authorRepository;
    private final UserService userService;

    @Autowired
    public BookController(BookRepository bookRepository, BookService bookService, AuthorRepository authorRepository, UserService userService) {
        this.bookRepository = bookRepository;
        this.bookService = bookService;
        this.authorRepository = authorRepository;
        this.userService = userService;
    }

     ArrayList<Author> authors;

    @GetMapping("/books")
    public String showBooksList(Model model, @RequestParam(name = "searchBook", required = false) String bookName,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "16") int size) {
        List<Book> books;
        Pageable paging = PageRequest.of(page, size);
        Page<Book> pageTuts;

        if (bookName != null) {
            //pagination
            pageTuts = bookRepository.findByKeyword(bookName, paging);
        } else {
            pageTuts = bookRepository.findAll(paging);
        }
        books = pageTuts.getContent();
        //sort books by title in alphabeticalorder
        model.addAttribute("currentPage", pageTuts.getNumber());
        model.addAttribute("totalItems", pageTuts.getTotalElements());
        model.addAttribute("totalPages", pageTuts.getTotalPages());
        model.addAttribute("books", books);

        return "books";
    }

    @GetMapping("/books/book-details/{bookId}")
    public String showBookDetails(Model model, @PathVariable("bookId") long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + bookId));
        boolean existInWishlist = false;
        //check if book is already into user's wishlist,
        User currUser = userService.getCurrentLoggedUser();
        List<Book> userWishlist = currUser.getWishlist();
        for (Book bok : userWishlist) {
            if (bok.getTitle().equals(book.getTitle())) {
                existInWishlist = true;
                break;
            }
        }
        //check if book is borrowed by current logged user and use it to show button
        boolean isBookCurrBorrowedByLoggedUser = false;
        Set<Report> userBorrows = currUser.getBorrowedBooks();
        for (Report rep : userBorrows) {
            if (rep.getBook().equals(book) && book.isIsRented()) {
                isBookCurrBorrowedByLoggedUser = true;
                break;
            }
        }
        model.addAttribute("book", book);
        model.addAttribute("user", currUser);
        model.addAttribute("existInWishlist", existInWishlist);
        model.addAttribute("isBookCurrBorrowedByLoggedUser", isBookCurrBorrowedByLoggedUser);

        return "book-details";
    }

    @GetMapping("/books/signup")
    public String showSignUpForm(Model model, @ModelAttribute("authors") ArrayList<Author> authors) {
        authors.addAll(authorRepository.findAll());
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authors);
        model.addAttribute("report", new Report());

        return "add-book";
    }

    @PostMapping("/books/addbook")
    public String addBook(@Valid Book book, BindingResult result, Model model,
                          @ModelAttribute("authors") ArrayList<Author> authors, @RequestParam("file") MultipartFile file,
                          @RequestParam("file2") MultipartFile file2) throws IOException {
        //current date
        Date dateAdded = new Date();
        System.out.print("book " + book);
        //if the list of authors for the dropdown is empty on refresh or redirect, then fill them
        if (authors.isEmpty()) {
            authors.addAll(authorRepository.findAll());
            model.addAttribute("authors", authors);
        }
        List<Book> books = bookRepository.findAll();
        //generate manually id of new book
        long bookLastId = books.get(books.size() - 1).getBookId();
        book.setBookId(bookLastId + 1);
        book.setDateAdded(dateAdded);

        //create a boolean which checks if the input isbn exists already in  the db, 
        //this custom validation is needed, because the @unique in the model checks only on model, not the form itself
        boolean dublicateIsbn = bookService.checkIfIsbnDublicate(book.getIsbn());
        //if there are validation errors or the 
        if (result.hasErrors() || dublicateIsbn) {

            return "add-book";
        }
        //picture attributes
        String fileName = file.getOriginalFilename();
        book.setBookPicture(fileName);
        book.setPictureContent(file.getBytes());
        book.setPictureSize(file.getSize());
        book.setFileContent(file2.getBytes());
        if (book.getPictureSize() >= 1048576) {
            model.addAttribute("success", "Picture is too big!!!");

            return "add-book";
        }
        model.addAttribute("success", "File Uploaded Successfully!!!");
        model.addAttribute("bookPicture", book.getBookPicture());
        model.addAttribute("content", book.getPictureContent());
        model.addAttribute("size", book.getPictureSize());
        model.addAttribute("success", "File Uploaded Successfully!!!");
        model.addAttribute("file", file);
        bookRepository.save(book);
        model.addAttribute("books", bookRepository.findAll());

        return "redirect:/books";
    }

    @GetMapping("/books/edit/{bookId}")
    public String showUpdateForm(@PathVariable("bookId") long bookId, Model model) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + bookId));
        List<Author> authors = authorRepository.findAll();

        MultipartFile multipartFile = new MockMultipartFile(book.getBookPicture(), book.getPictureContent());
        model.addAttribute("file1", multipartFile);
        model.addAttribute("bok", book);
        model.addAttribute("authors", authors);

        return "update-book";
    }

    @PostMapping("/books/update/{bookId}")
    public String updateBook(@PathVariable("bookId") long bookId, @Valid Book book,
                             BindingResult result, Model model, @RequestParam("file") MultipartFile file,
                             @RequestParam("file2") MultipartFile file2) throws IOException {
        if (result.hasErrors()) {
            System.out.println("Id of boook to be edited : " + book.getBookId());
            book.setBookId(book.getBookId());
            System.out.println("set Id  : " + book.getBookId());
            model.addAttribute("bookPicture", book.getBookPicture());
            model.addAttribute("content", book.getPictureContent());
            model.addAttribute("size", book.getPictureSize());
            model.addAttribute("bok", book);
            model.addAttribute("authors", authors);

            return "update-book";
        }
        if (bookService.checkIfIsbnDublicateEdit(book.getIsbn(), bookId)) {
            book.setBookId(bookId);

            return "redirect:/books/edit/{bookId}";
        }

        //picture attributes, for multipart file
        if (file == null && book.getBookPicture() != null) {
            book.setBookPicture(book.getBookPicture());
            book.setPictureContent(book.getPictureContent());
            book.setPictureSize(book.getPictureSize());
            book.setFileContent(book.getFileContent());
        } else {
            book.setBookPicture(file.getOriginalFilename());

            try {
                book.setPictureContent(file.getBytes());
            } catch (IOException ex) {
                Logger.getLogger(BookController.class.getName()).log(Level.SEVERE, null, ex);
            }
            book.setPictureSize(file.getSize());
            book.setFileContent(file2.getBytes());
        }
        model.addAttribute("success", "File Uploaded Successfully!!!");
        //save the data in  the db
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

    //gets picture, pic is downloaded when link is loaded
    @GetMapping("/image")
    public void showImage(@Param("id") Long id, HttpServletResponse response)
            throws IOException {

        Optional<Book> book = bookService.findById(id);
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif, image/pdf, application/pdf");
        response.getOutputStream().write(book.get().getPictureContent());
        response.addHeader("content-disposition", "inline; filename=\"" + book.get().getBookPicture());
        response.getOutputStream().close();
    }

    @GetMapping("books/b/files")
    public void showBookContentInBrowser(@Param("id") Long id, HttpServletResponse response) throws IOException {
        Optional<Book> book = bookService.findById(id);
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif, image/pdf, application/pdf");
        response.getOutputStream().write(book.get().getFileContent());
        response.addHeader("content-disposition", "inline");
        response.getOutputStream().close();
    }

    @GetMapping("/available-books")
    public String showAvailableBooksList(Model model, @RequestParam(name = "searchBook", required = false) String bookName,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        List<Book> books;

        Pageable paging = PageRequest.of(page, size);
        Page<Book> pageTuts;
        //if there are no available searched titles, then return all search results
        if (bookName != null) {
            pageTuts = bookRepository.findByKeyword(bookName, paging);
        } else {
            pageTuts = bookRepository.findByIsRented(false, paging);
        }

        books = pageTuts.getContent();
        model.addAttribute("currentPage", pageTuts.getNumber());
        model.addAttribute("totalItems", pageTuts.getTotalElements());
        model.addAttribute("totalPages", pageTuts.getTotalPages());
        model.addAttribute("books", books);

        return "books";
    }

    @GetMapping("/recently-added")
    public String showNewBooksList(Model model, @RequestParam(name = "searchBook", required = false) String bookName,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        List<Book> books;
        Pageable paging =  PageRequest.of(page, size);
        Page<Book> pageTuts;
        //if there are no available searched titles, then return all search results
        if (bookName != null) {
            pageTuts = bookRepository.findByKeyword(bookName, paging);
        } else {
            pageTuts = bookService.listNewBooks(paging);
        }

        books = pageTuts.getContent();
        model.addAttribute("currentPage", pageTuts.getNumber());
        model.addAttribute("totalItems", pageTuts.getTotalElements());
        model.addAttribute("totalPages", pageTuts.getTotalPages());
        model.addAttribute("books", books);

        return "books";
    }

    @GetMapping("/genres/{genreName}")
    public String loadGenresPage(Model model,
                                 @PathVariable("genreName") String genre,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "16") int size,
                                 @RequestParam(name = "searchBook", required = false) String bookName) {
        List<Book> books;

        Pageable paging = (Pageable) PageRequest.of(page, size);
        Page<Book> pageTuts = null;
        List<String> genresList = new ArrayList<>();
        //if there are no available searched titles, then return all search results
        if (bookName != null) {
            pageTuts = bookRepository.findByKeyword(bookName, paging);
            books = pageTuts.getContent();
        } else {
            if (genre.equalsIgnoreCase("other")) {
                pageTuts = bookRepository.findAll(paging);
                genresList = bookService.createGenresList(genresList);
                books = bookService.getOtherGenreTitles(genresList, paging);
                pageTuts = new PageImpl<>(books);
            } else {
                pageTuts = bookRepository.findByGenres(genre, paging);
                books = pageTuts.getContent();
            }
        }
        model.addAttribute("currentPage", pageTuts.getNumber());
        model.addAttribute("totalItems", pageTuts.getTotalElements());
        model.addAttribute("totalPages", pageTuts.getTotalPages());
        model.addAttribute("books", books);
        model.addAttribute("subtitle", genre);

        return "books";
    }

    @PostMapping("/books/createRequestToBorrow")
    public String requestABookToBeBorrowed(@Valid Book book) {
        bookService.createABorrowRequest(book);

        return "redirect:/user/borrow-requests";
    }
}
