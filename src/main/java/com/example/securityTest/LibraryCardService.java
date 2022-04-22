/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.securityTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author user
 */
@Service
public class LibraryCardService {
    
     @Autowired
    LibraryCardRepository cardRepository;


    List<LibraryCard> findAll() {
        return cardRepository.findAll();
    }
    
      List<LibraryCard> findByCardNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber);
    }
    
     
     
     
    //check if there are any any cards to expire and return list of them
    List<LibraryCard> getListOfCardsAboutToExpire() {

        List<LibraryCard> cards = findAll();
        List<LibraryCard> expiringCards = new ArrayList<>();

        cards.stream().forEach(card -> {
            System.out.println(card);
           
            boolean isExpired = checkIfCardExpires(card);
            if (isExpired) {
                expiringCards.add(card);
            }
        } );
        return expiringCards;
    }


     //when expiration date comes, disable user
     Boolean checkIfCardExpires(LibraryCard card){
        
         
         Date currentDate = new Date();        
   // LibraryCard card = findByCardNumber( cardNumber).get(0);       
    //chech if current date is before card expiration date, if it is, then make the card expired
            boolean alo = card.getCardExpirationDate().before(currentDate );
             if (alo) {
               
               
            } 
             return alo;
    
}
      void expireCard( LibraryCard card, boolean isItExpirationDate){
       if (isItExpirationDate) {
                System.out.println("card is to expire! :  " + card.getCardNumber());
                //put in a method which sends an email to the user later
                card.setIsExpired(true);
                cardRepository.save(card);
               
            } 
      
      }
}
