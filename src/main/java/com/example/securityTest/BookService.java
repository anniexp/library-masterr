/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.securityTest;

import java.util.ArrayList;
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
    
     
    
    
}
