/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.securityTest;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import static java.util.Collections.list;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

/**
 *
 * @author Lenovo
 */
@Service
public class BookService {

    @Autowired
    BookRepository bookRepository;
    
    @Autowired
    UserService userService;

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
            }
        }
        return isDublicate;
    }

    public Boolean checkIfIsbnDublicateEdit(String inputIsbn, long currentId) {
        List<Book> books = bookRepository.findAll();
        boolean isDublicate = false;
        for (Book book : books) {
            System.out.println("Current id " + book.getBookId() + "   ");
            System.out.println(currentId);
            System.out.println(inputIsbn);
            System.out.println(book.getIsbn());

            if ((book.getIsbn().matches(inputIsbn)) && (book.getBookId() != currentId)) //if (book.getIsbn().matches(inputIsbn))           
            {
                isDublicate = true;

            }
        }
        System.out.println(isDublicate);

        return isDublicate;
    }

    //create a date number of days before input date
    public Date createDateBeforeCurrentDate(LocalDate localDate, int numberOfDays) {
        LocalDate localDateBefore = localDate.minusDays(numberOfDays);
        Instant instant = localDateBefore.atTime(LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault()).toInstant();
        Date dateBefore = Date.from(instant);
        System.out.print("new date :  " + dateBefore);

        return dateBefore;
    }

    //creates a list of all currently available books 
    public List<Book> listAvailableBooks() {
        List<Book> books = bookRepository.findAll();
        System.out.println("all books :  " + books.size());
        List<Book> filtererBooks = new ArrayList<Book>();
        ListIterator<Book> listIterator = books.listIterator();

        books.stream().forEach(elem -> System.out.println(elem));

        for (Book book : books) {
            /* if(book.toString().equalsIgnoreCase(null)){
            continue;}*/

            boolean alo = book.isIsRented();
            System.out.println("is it rented :  " + alo);

            if (alo) {
            } else {
                filtererBooks.add(book);
                System.out.println("available books :  " + filtererBooks.size());
            }

        }
        System.out.println("available books :  " + filtererBooks.size());
        return filtererBooks;

    }

    //creates a list of all new books 
    public Page<Book> listNewBooks(Pageable paging) {
        List<Book> books = bookRepository.findAll();
        System.out.println("all books :  " + books.size());
        List<Book> filtererBooks = new ArrayList<Book>();
        ListIterator<Book> listIterator = books.listIterator();

        //all new books will be added max 14 days ago
        LocalDate currDate = LocalDate.now();
        // date is set to 1 day ago
        Date dateForNewBooks = createDateBeforeCurrentDate(currDate, 1);

        books.stream().forEach(elem -> {
            System.out.println(elem);
            Date elemDateAdded = elem.getDateAdded();

            //if the date the book was added is less than 14 days ago, then add it to the list of new books
            boolean alo = dateForNewBooks.before(elemDateAdded);

            System.out.println("Has the book been added less than 14 days ago? : "
                    + dateForNewBooks.before(elemDateAdded));

            if (alo) {
                System.out.println("yes! :  " + alo);
                filtererBooks.add(elem);
            } else {
                System.out.println("no! :  " + alo);

            }

        });

        System.out.println("new books :  " + filtererBooks.size());
        Page<Book> page = new PageImpl<>(filtererBooks);

        return page;

    }

    List <Book> addToWishList(Book book) {
       
       User currUser =  userService.getCurrentLoggedUser();
       List<Book> wishlist= currUser.getWishlist();
       System.out.println("Wishlist size before add :" + wishlist);
       //if the title is not present yet, add it
       if(!wishlist.contains(book)){
       wishlist.add(book);
       
       }
              System.out.println("Wishlist size after add :" + wishlist);
              

       return wishlist;
    }
    
     List <Book> removeBookFromWishList(Book book) {
       
       User currUser =  userService.getCurrentLoggedUser();
       List<Book> wishlist= currUser.getWishlist();
       System.out.println("Wishlist size before add :" + wishlist);
       //if the title is not present yet, add it
       if(wishlist.contains(book)){
       wishlist.remove(book);
       
       }
              System.out.println("Wishlist size after add :" + wishlist);
              

       return wishlist;
    }

    List <Book> createABorrowRequest(Book book) {
         User currUser =  userService.getCurrentLoggedUser();
       List<Book> borrowRequests= currUser.getBorrowRequests();
       System.out.println("Borrow requests size before add :" + borrowRequests);
       //if the title is not present yet, add it
       if(!borrowRequests.contains(book)){
       borrowRequests.add(book);
       
       }
              System.out.println("Wishlist size after add :" + borrowRequests);

       return borrowRequests;
        
         }

    public void showImage(HttpServletResponse response, Book book)
            throws ServletException, IOException {

        response.setContentType("image/jpeg, image/jpg, image/png, image/gif, image/pdf");
        response.getOutputStream().write(book.getPictureContent());

    }

    Page<Book> findByGenres(String genre, Pageable pageable) {

        return bookRepository.findByGenres(genre,pageable);
    }

    //creates a list which gets the n first elements of a list
    List<Book> getFirstNBooksList(List<Book> books, Integer n) {

        List<Book> leading = books.subList(0, n - 1);
        System.out.println("Size of list: " + leading.size());

        return leading;
    }

    
       
    
    //split a list into 3lists, which will fill in a grid the views
    public void splitStringListIntoSeveralLists(List<String> strList,List<String> firstList, List<String> secondList,List<String> thirdList) {
        //genetare n lists
        /*for (int i = 0, i < numberOfColumns, i++) {
            List<Book> book= null;
        }*/
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
  
     List<String> createGenresList(    List<String> genresList
){
    for (Genres genre : Genres.values()) { 
            
          String gen = genre.toString();
            genresList.add(gen);
            System.out.println("Numbers of elements: " +genresList.size() );
        }
        System.out.println(genresList);
                   
                    genresList.sort(String.CASE_INSENSITIVE_ORDER);  //sorts the list in case insensitive order
                            System.out.println(genresList);

                    genresList.sort(Comparator.naturalOrder());    //sorts list in ascending order  
                    
                    return genresList;
                    
    }
    
    
    
    public Page<Book> findSorted(final int pageNumber,final int pageSize,
            final String sortField, final String sortDirection){
        final Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name())?
                Sort.by(sortField).ascending(): Sort.by(sortField).descending();
final Pageable pageable = PageRequest.of(pageNumber -1, pageSize, sort);
    return bookRepository.findAll(pageable);
    
    }
    
    
    List<Book> getOtherGenreTitles(List<String> genresList, Page<Book> pageTuts,
            Pageable paging
    ) {
        Page<Book> otherGenreBooks = bookRepository.findAll(paging);
       // List<Book> allBooks = otherGenreBooks.getContent();
        List<Book> allBooks = bookRepository.findAll();
      //  genresList = createGenresList(genresList);
        List<Book> genreBooks;
        List<Book> allGenreBooks = new ArrayList<>();
        List<Book> otheGenreBooks = new ArrayList<>();

        for (String genre : genresList) {

            pageTuts = findByGenres(genre, paging);
            genreBooks = pageTuts.getContent();
            for (Book genr : genreBooks) {
                allGenreBooks.add(genr);

            }

        }

        System.out.println("Size of genre books: " + allGenreBooks.size());
        System.out.println("all books: " + allBooks);
      
              
              
            for (Book book : allBooks) {
           
                       if (allGenreBooks.contains(book)==false){
                    otheGenreBooks.add(book);
                     System.out.println("Added book into other genre list  : " + book);
              
          
           System.out.println("Size of list of other genre books: " + otheGenreBooks.size());
            }}
        return otheGenreBooks;
    }
    
}
