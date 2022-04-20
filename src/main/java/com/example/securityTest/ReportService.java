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
import static java.util.Collections.list;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

/**
 *
 * @author Lenovo
 */
@Service
public class ReportService {

    @Autowired
    ReportRepository reportRepository;
    @Autowired
    BookService bookService;
    @Autowired
    BookRepository bookRepository;

    List<Report> findByReportId(long reportId) {
        return reportRepository.findByReportId(reportId);
    }

    public void save(Report report) {
        reportRepository.save(report);
    }

    //create a date number of days after input date
    public Date createDateAfterDate(LocalDate localDate, int numberOfDays) {
        LocalDate dateReturn = localDate.plusDays(numberOfDays);
        Instant instant = dateReturn.atTime(LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault()).toInstant();
        Date dateBookReturn = Date.from(instant);
        System.out.print("new date :  " + dateBookReturn);

        return dateBookReturn;
    }
    
 
    
}
