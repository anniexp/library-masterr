package com.example.securityTest;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;

import java.util.Date;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Lenovo
 */
@Controller
public class ReportController {
    private final ReportRepository reportRepository;
    private final BookService bookService;
    private final UserRepository userRepository;
    private final HttpServletRequest request;
    private final ReportService reportService;

    @Autowired
    public ReportController(ReportRepository reportRepository, BookService bookService, UserRepository userRepository, HttpServletRequest request, ReportService reportService) {
        this.reportRepository = reportRepository;
        this.bookService = bookService;
        this.userRepository = userRepository;
        this.request = request;
        this.reportService = reportService;
    }

    @GetMapping("/reports")
    public String index(Model model) {
        List<Report> reports = reportRepository.findAll();
        model.addAttribute("reports", reports);

        return "reports";
    }

    @GetMapping("/reports/new-report")
    public String showNewReportForm(Model model) {
        List<Book> book = bookService.listAvailableBooks();
        List<User> users = userRepository.findAll();
        // Principal currentEmployee = UserController.getCurrentUser(request);
        model.addAttribute("book", book);
        model.addAttribute("users", users);
        model.addAttribute("report", new Report());

        return "add-report";
    }

    @PostMapping("/reports/add_report")
    public String addReport(@Valid Report rep, BindingResult result, Model model) {
        Date dateCreated = new Date();
        LocalDate currDate = LocalDate.now();
        List<Report> reports = reportRepository.findAll();
        long reportLastId = reports.get(reports.size() - 1).getReportId();

        rep.setReportId(reportLastId + 1);
        rep.setDateCreated(dateCreated);
        rep.setLastUpdated(reportService.createDateAfterDate(currDate, 14));
        rep.setIsReturned(false);
        //if there are validation errors or the 
        if (result.hasErrors()) {

            return "add-report";
        }
        Book bookToUpdate = rep.getBook();
        bookToUpdate.setIsRented(true);
        bookService.save(bookToUpdate);

        User us = rep.getBorrower();
        List<Book> usBorrwReq = us.getBorrowRequests();

        List<Book> newBorrList = new ArrayList<>();
        for (Book obj : usBorrwReq) {
            if (obj.equals(bookToUpdate)) {
                newBorrList.add(obj);
            }
        }
        usBorrwReq.removeAll(newBorrList);
        us.setBorrowRequests(usBorrwReq);
        userRepository.save(us);
        reportService.save(rep);
        model.addAttribute("reports", reportRepository.findAll());

        return "redirect:/reports";
    }

    @GetMapping("/reports/edit/{reportId}")
    public String showUpdateForm(@PathVariable("reportId") long reportId, Model model) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid report Id:" + reportId));

        //create a list of all currently available plus the current book
        List<Book> book = bookService.listAvailableBooks();
        book.add(report.getBook());

        model.addAttribute("dateCreated", report.getDateCreated());
        model.addAttribute("dateReturned", report.getLastUpdated());

        List<User> users = userRepository.findAll();
        Principal currentEmployee = UserController.getCurrentUser(request);

        // User currentUserEmployee= (User) currentEmployee;
        //if it doesnt work, then to build it with a constructor, if that does not work, 
        //then firstly get the name of the cuurent user, then loop all users, if the
        //name equals, then this is the cuurent user 
        //User currentUserEmployee= new User(currentEmployee., String password,
        //String username, String rolee, boolean enabled);

        model.addAttribute("book", book);
        model.addAttribute("users", users);
        model.addAttribute("report", report);

        return "update-report";
    }

    @PostMapping("/reports/update/{reportId}")
    public String updateReport(@Valid Report report, BindingResult result, Model model) {
        if (result.hasErrors()) {
            report.setReportId(report.getReportId());

            return "update-report";
        }


        Book bookToUpdate = report.getBook();
        bookToUpdate.setIsRented(false);
        bookService.save(bookToUpdate);
        reportService.save(report);
        model.addAttribute("reports", reportRepository.findAll());

        return "redirect:/reports";
    }

    @GetMapping("/reports/delete/{reportId}")
    public String deleteBook(@PathVariable("reportId") long reportId, Model model) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid report Id:" + reportId));

        Book bookToUpdate = report.getBook();
        bookToUpdate.setIsRented(false);
        bookService.save(bookToUpdate);
        reportRepository.delete(report);
        model.addAttribute("reports", reportRepository.findAll());

        return "redirect:/reports";
    }
}
