/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.securityTest;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Lenovo
 */
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
      @Autowired
    LibraryCardService cardService;
    
     public Boolean checkIfUserExists(String inputUsername){
     List<User> users = userRepository.findAll();
     boolean isDublicate = false;
     for( User user:users){
         if (user.getUsername().matches(inputUsername))
         {
         isDublicate = true;
         }
        }
     return isDublicate;
    }
     
      public Boolean checkIfUserWithSameEmailExists(String inputEmail){
     List<User> users = userRepository.findAll();
     boolean isDublicate = false;
     for( User user:users){
         if (user.getEmail().matches(inputEmail))
         {
         isDublicate = true;
         }
        }
     return isDublicate;
    }
     
       List<User> findByUsername(String username) {
           
        return userRepository.findByUsername(username);
    }
       
       
     public Boolean checkIfUserInfoMatchWithCardInfo(String inputFirstName, String inputLastName,String inputCardNumber){
     List<LibraryCard> cards = cardService.findAll();
     boolean matches = false;
     
     //if the input data matches the a card records in the db, then the user is a valid client of the library and registartion can proceeds
     for( LibraryCard card:cards){
         if (card.getCardNumber().matches(inputCardNumber)&&card.getFirstName().matches(inputFirstName)&&card.getLastName().matches(inputLastName))
         {
        matches= true;
         }
        }
     return matches;
    }
     
     
     
       List<User> findByCardNumber(String cardNumber) {
           
        return userRepository.findByCardNumber(cardNumber);
    }

    
}
