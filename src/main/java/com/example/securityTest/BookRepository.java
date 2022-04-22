package com.example.securityTest;

import java.util.ArrayList;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.ListIterator;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitle(String title);
    List<Book> findByTitleStartingWith(String title);
    Page<Book> findByAuthor(Author author, Pageable pageable);
    
     List<Book> findByGenres(String genre);

    
    //Custom query
    @Query(value = "select * from book s where s.title like %:keyword% ", nativeQuery = true)
    List<Book> findByKeyword(@Param("keyword") String keyword);
    
    

    //search methods with pagination, returns a page with them, not all
     Page<Book> findByTitle(String title, Pageable pageable);
     Page<Book> findByTitleStartingWith(String title, Pageable pageable);
    //Custom query
    @Query(value = "select * from book s where s.title like %:keyword% ", nativeQuery = true)
    Page<Book> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
     Page<Book> findByIsRented (boolean isRented,Pageable pageable);

    
    
    
}
