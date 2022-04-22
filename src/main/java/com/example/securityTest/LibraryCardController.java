/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.securityTest;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author user
 */
public class LibraryCardController {
    
    
    @Autowired
    LibraryCardRepository cardRepository;

    @Autowired
    LibraryCardService cardService;
    
    @Autowired
    ReportService reportService;
    
    @GetMapping("/library_cards")
    public String index(Model model) {
        List<LibraryCard> cards = null;
        if (cards != null) {
            //reports = bookService.findByKeyword(bookName);
        } else {
            cards = cardService.findAll();
        }
        model.addAttribute("cards", cards);
       
        return "library-cards";
    }
    
    @GetMapping("/library_cards/new-card")
    public String showNewCardForm(Model model
    ) {
        
        List<LibraryCard> cards = cardService.findAll();        
        //String username, String rolee, boolean enabled);
        System.out.println("Number of cards : " + cards.size());
        model.addAttribute("cards", cards);
    
        return "add-card";
    }
    
    @PostMapping("/library_cards/add_card")
    public String addCard(@Valid LibraryCard card, BindingResult result, Model model
    ) {
        //current date
        Date dateCreated = new Date();
        LocalDate currDate = LocalDate.now();

        //boolean isBookRented = bok.isIsRented();
        // System.out.println("Is the current book rented - " + bok.isIsRented());
         List<LibraryCard> cards = cardService.findAll();   
        
        long cardsLastId = cards.get(cards.size() - 1).getCardId();
        System.out.println("current last card id is: " + cardsLastId);
        
        card.setCardId(cardsLastId + 1);
        System.out.print("report :  " + card);
        
        card.setDateCreated(dateCreated);
        System.out.println("current date :  " + dateCreated);
        /*
        //set date of return of the bookto be 14 days after the current date 
            LocalDate dateReturn =  LocalDate.now().plusDays(14);  
            Instant instant = dateReturn.atTime(LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault()).toInstant();
            Date dateBookReturn = Date.from(instant);
         */
        
        Date expirationDate = reportService.createDateAfterDate(currDate, 365);
        System.out.println("return date :  " + expirationDate);
        
        card.setCardExpirationDate(expirationDate);
        System.out.println("expiration date :  " + expirationDate);
        
        card.setIsExpired(false);

        /*
        if (isBookRented == false){
        reportService.save(report);    
        return "redirect:/reports";
    }else {
        
        return "add-report";
        }*/
        //if there are validation errors or the 
        if (result.hasErrors()) {
            
            return "add-report";
        }
                     
        cardRepository.save(card);
        
        model.addAttribute("cards", cardService.findAll());
        
        return "redirect:/library-cards";
    }
    
    
}
