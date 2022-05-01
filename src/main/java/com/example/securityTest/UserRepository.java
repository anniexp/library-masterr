/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.securityTest;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    List<User> findByUsername(String username);
   

     List<User> findByCardNumber(String cardNumber);
     
     
      List<User> findByEnabled(boolean enabled);
      Page<User> findByEnabled (boolean enabled,Pageable pageable);
      
      List<User> findByRolee(String rolee);
      Page<User> findByRolee (String rolee,Pageable pageable);
    
 Page<User> findByUsername(String username,Pageable pageable);
   Page<User> findByCardNumber(String cardNumber, Pageable pageable);
     
   @Query(value = "select * from users s where s.rolee like %:rolee% ", nativeQuery = true)
    Page<User> findByKeywordRole(@Param("rolee") Rolee rolee, Pageable pageable);
    
     @Query(value = "select * from users_borrow_requests s", nativeQuery = true)
    Page<Book> findAllBorrowRequests(Pageable pageable);
    @Query(value = "select * from users_borrow_requests s", nativeQuery = true)
    List<Book> findAllBorrowRequests();
    
}
