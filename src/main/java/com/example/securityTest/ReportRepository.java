/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.securityTest;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
    @Repository
public interface ReportRepository extends JpaRepository<Report, Long>{
    
        List<Report> findByReportId(long reportId);

        List<Report> findByBorrower(User currUser);
        
//Custom query
    @Query(value = "select * from report s where s.user_id = :keyword and s.is_returned = 'false'", nativeQuery = true)
    List<Report> findByKeyword(String keyword);
    
}
