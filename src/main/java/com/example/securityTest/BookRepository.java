package com.example.securityTest;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitle(String title);

    List<Book> findByTitleStartingWith(String title);
    //Custom query

    @Query(value = "select * from book s where s.title like %:keyword% ", nativeQuery = true)
    List<Book> findByKeyword(@Param("keyword") String keyword);

}
