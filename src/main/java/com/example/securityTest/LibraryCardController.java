package com.example.securityTest;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author user
 */
@Controller
public class LibraryCardController {
    LibraryCardRepository cardRepository;
    LibraryCardService cardService;
    ReportService reportService;

    @Autowired
    public LibraryCardController(LibraryCardRepository cardRepository, LibraryCardService cardService, ReportService reportService) {
        this.cardRepository = cardRepository;
        this.cardService = cardService;
        this.reportService = reportService;
    }

    @GetMapping("/library_cards")
    public String index(Model model) {
        List<LibraryCard> cards = cardService.findAll();
        model.addAttribute("cards", cards);
       
        return "library-cards";
    }
    
    @GetMapping("/library_cards/new-card")
    public String showNewCardForm(Model model) {
        LibraryCard card = new LibraryCard();
        model.addAttribute("card", card);
    
        return "add-card";
    }
    
    @PostMapping("/library_cards/add_card")
    public String addCard(@Valid LibraryCard card, BindingResult result, Model model
    ) {
        Date dateCreated = new Date();
        LocalDate currDate = LocalDate.now();
        List<LibraryCard> cards = cardService.findAll();
        
        long cardsLastId = cards.get(cards.size() - 1).getCardId();
        System.out.println("current last card id is: " + cardsLastId);
        
        card.setCardId(cardsLastId + 1);
        card.setDateCreated(dateCreated);
        Date expirationDate = reportService.createDateAfterDate(currDate, 365);
        card.setCardExpirationDate(expirationDate);
        card.setIsExpired(false);

        if (result.hasErrors()) {

            return "add-card";
        }
                     
        cardRepository.save(card);
        model.addAttribute("cards", cardService.findAll());
        
        return "redirect:/";
    }
}
