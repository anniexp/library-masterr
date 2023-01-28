package com.example.securityTest;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 *
 * @author Lenovo
 */
@Service
public class ReportService {
    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

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

     List<Report> findByBorrower(User currUser) {
        return reportRepository.findByBorrower(currUser);
    }
      
    List<Report> findByKeyword(String id) {
        return reportRepository.findByKeyword(id);
    }
}
