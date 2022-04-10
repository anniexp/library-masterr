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
public interface AuthorRepository extends JpaRepository<Author, Long>{
        List<Author> findByAuthorName(String authorName);
        List<Author> findByAuthorNameStartingWith(String authorName);       
         //Custom query
 @Query(value = "select * from authors s where s.author_name like %:authorName% ", nativeQuery = true)
 List<Author> findByKeyword(@Param("authorName") String authorName);
 
 @Query(value = "select * from authors s where s.author_name like %:authorName% ", nativeQuery = true)
     Page<Author> findByKeyword(@Param("authorName") String authorName, Pageable paging);

}
