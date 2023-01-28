package com.example.securityTest;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * @author Lenovo
 */
@Service
public class BookService {
    private final BookRepository bookRepository;
    private final UserService userService;

    @Autowired
    public BookService(BookRepository bookRepository, UserService userService) {
        this.bookRepository = bookRepository;
        this.userService = userService;
    }

    List<Book> findByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    List<Book> findByTitleStartingWith(String title) {
        return bookRepository.findByTitleStartingWith(title);
    }

    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    public void delete(Book book) {
        bookRepository.delete(book);
    }

    public void save(Book book) {
        bookRepository.save(book);
    }

    public List<Book> findByKeyword(String keyword) {
        return bookRepository.findByKeyword(keyword);
    }

    public Boolean checkIfIsbnDublicate(String inputIsbn) {
        List<Book> books = bookRepository.findAll();
        boolean isDublicate = false;
        for (Book book : books) {
            if (book.getIsbn().matches(inputIsbn)) {
                isDublicate = true;
                break;
            }
        }

        return isDublicate;
    }

    public Boolean checkIfIsbnDublicateEdit(String inputIsbn, long currentId) {
        List<Book> books = bookRepository.findAll();
        boolean isDublicate = false;
        for (Book book : books) {
            if ((book.getIsbn().matches(inputIsbn)) && (book.getBookId() != currentId)) {
                isDublicate = true;
                break;
            }
        }

        return isDublicate;
    }

    //create a date number of days before input date
    public Date createDateBeforeCurrentDate(LocalDate localDate, int numberOfDays) {
        LocalDate localDateBefore = localDate.minusDays(numberOfDays);
        Instant instant = localDateBefore.atTime(LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault()).toInstant();

        return Date.from(instant);
    }

    //creates a list of all currently available books 
    public List<Book> listAvailableBooks() {
        List<Book> books = bookRepository.findAll();
        List<Book> filtererBooks = new ArrayList<Book>();

        books.forEach(System.out::println);

        for (Book book : books) {
            boolean alo = book.isIsRented();

            if (alo) {
            } else {
                filtererBooks.add(book);
            }
        }

        return filtererBooks;
    }

    //creates a list of all new books 
    public Page<Book> listNewBooks(Pageable paging) {
        List<Book> books = bookRepository.findAll();
        List<Book> filtererBooks = new ArrayList<Book>();
        //all new books will be added max 14 days ago
        LocalDate currDate = LocalDate.now();
        // date is set to 1 day ago
        Date dateForNewBooks = createDateBeforeCurrentDate(currDate, 1);

        books.forEach(elem -> {
            Date elemDateAdded = elem.getDateAdded();
            //if the date the book was added is less than 14 days ago, then add it to the list of new books
            boolean alo = dateForNewBooks.before(elemDateAdded);

            if (alo) {
                filtererBooks.add(elem);
            }
        });

        return new PageImpl<>(filtererBooks);
    }

    List<Book> addToWishList(Book book) {

        User currUser = userService.getCurrentLoggedUser();
        List<Book> wishlist = currUser.getWishlist();
        //if the title is not present yet, add it
        if (!wishlist.contains(book)) {
            wishlist.add(book);
        }
        return wishlist;
    }

    List<Book> removeBookFromWishList(Book book) {

        User currUser = userService.getCurrentLoggedUser();
        List<Book> wishlist = currUser.getWishlist();
        //if the title is not present yet, add it
        if (wishlist.contains(book)) {
            wishlist.remove(book);
        }

        return wishlist;
    }

    List<Book> createABorrowRequest(Book book) {
        User currUser = userService.getCurrentLoggedUser();
        List<Book> borrowRequests = currUser.getBorrowRequests();
        //if the title is not present yet, add it
        if (!borrowRequests.contains(book)) {
            borrowRequests.add(book);
        }

        return borrowRequests;
    }

    public void showImage(HttpServletResponse response, Book book) throws IOException {
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif, image/pdf");
        response.getOutputStream().write(book.getPictureContent());
    }

    Page<Book> findByGenres(String genre, Pageable pageable) {
        return bookRepository.findByGenres(genre, pageable);
    }

    //creates a list which gets the n first elements of a list
    List<Book> getFirstNBooksList(List<Book> books, Integer n) {
        List<Book> leading = books.subList(0, n - 1);
        System.out.println("Size of list: " + leading.size());

        return leading;
    }

    //split a list into 3lists, which will fill in a grid the views
    public void splitStringListIntoSeveralLists(List<String> strList, List<String> firstList, List<String> secondList, List<String> thirdList) {
        //genetare n lists

        //modulo operation(if remainder is 0, add to first list, if 1 - to second and if 2- yo third)
        for (int j = 0; j < strList.size(); j++) {
            switch (j % 3) {
                case 0:
                    firstList.add(strList.get(j));
                    break;
                case 1:
                    secondList.add(strList.get(j));
                    break;
                case 2:
                    thirdList.add(strList.get(j));
                    break;
                default:
                    System.out.println("Null element!");
                    break;
            }
        }
    }

    List<String> createGenresList(List<String> genresList) {
        for (Genres genre : Genres.values()) {
            String gen = genre.toString();
            genresList.add(gen);
        }
        genresList.sort(String.CASE_INSENSITIVE_ORDER);  //sorts the list in case insensitive order
        genresList.sort(Comparator.naturalOrder());    //sorts list in ascending order

        return genresList;
    }

    public Page<Book> findSorted(final int pageNumber, final int pageSize,
                                 final String sortField, final String sortDirection) {
        final Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        final Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        return bookRepository.findAll(pageable);
    }

    List<Book> getOtherGenreTitles(List<String> genresList, Pageable paging) {
        List<Book> allBooks = bookRepository.findAll();
        List<Book> genreBooks;
        List<Book> allGenreBooks = new ArrayList<>();
        List<Book> otheGenreBooks = new ArrayList<>();

        for (String genre : genresList) {
            Page<Book> pageTuts = findByGenres(genre, paging);
            genreBooks = pageTuts.getContent();
            allGenreBooks.addAll(genreBooks);
        }
        for (Book book : allBooks) {
            if (!allGenreBooks.contains(book)) {
                otheGenreBooks.add(book);
            }
        }

        return otheGenreBooks;
    }
}
