/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.securityTest;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Lenovo
 */
@Service
public class BookService {

    @Autowired
    BookRepository bookRepository;
    
  

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
    
    public Boolean checkIfIsbnDublicate(String inputIsbn){
     List<Book> books = bookRepository.findAll();
     boolean isDublicate = false;
     for( Book book:books){
         if (book.getIsbn().matches(inputIsbn))
         {
         isDublicate = true;
         }
        }
     return isDublicate;
    }
    
    public Boolean checkIfIsbnDublicateEdit(String inputIsbn, long currentId){
     List<Book> books = bookRepository.findAll();
     boolean isDublicate = false;
     for( Book book:books){
         System.out.println("Current id "+ book.getBookId() + "   ");
         System.out.println(currentId);
         System.out.println(inputIsbn);
         System.out.println(book.getIsbn());

         if ((book.getIsbn().matches(inputIsbn))&& (book.getBookId()!=currentId))
         //if (book.getIsbn().matches(inputIsbn))           
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
            } 
           
            else {
                filtererBooks.add(book);
                System.out.println("available books :  " + filtererBooks.size());
            }
        
        }
        System.out.println("available books :  " + filtererBooks.size());
        return filtererBooks;

    }
    
     //creates a list of all currently available books 
    public Page<Book> listNewBooks(Pageable paging) {
        List<Book> books = bookRepository.findAll();
        System.out.println("all books :  " + books.size());
        List<Book> filtererBooks = new ArrayList<Book>();
        ListIterator<Book> listIterator = books.listIterator();
        
        //all new books will be added max 14 days ago
        LocalDate currDate = LocalDate.now();
        // date is set to 1 day ago
        Date dateForNewBooks =  createDateBeforeCurrentDate(currDate, 1);
        
      
        books.stream().forEach(elem -> {
            System.out.println(elem);
             Date elemDateAdded = elem.getDateAdded();
             
            //if the date the book was added is less than 14 days ago, then add it to the list of new books
            boolean alo =  dateForNewBooks.before(elemDateAdded);
            
             System.out.println("Has the book been added less than 14 days ago? : "
                         + dateForNewBooks.before(elemDateAdded));
           
           
            if (alo) {
                 System.out.println("yes! :  " + alo);
                   filtererBooks.add(elem);
            } 
                 else {
                 System.out.println("no! :  " + alo);
                 
            } 
            
        });
      
        System.out.println("new books :  " + filtererBooks.size());
        Page<Book> page = new PageImpl<>(filtererBooks);
        
        return page;

    }

    void addToWishList(Book book) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    void createABorrowRequest(Book book) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
     
    
    
}
