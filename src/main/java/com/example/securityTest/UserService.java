/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.securityTest;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Boolean checkIfUserWithUsernameExists(List<User> users ,String inputUsername) {
        boolean isDublicate = false;
        for (User user : users) {
            if (user.getUsername().matches(inputUsername)) {
                isDublicate = true;
            }
        }
        return isDublicate;
    }

    public Boolean checkIfUserWithSameEmailExists(List<User>users, String inputEmail) {
        boolean isDublicate = false;
        for (User user : users) {
            if (user.getEmail().matches(inputEmail)) {
                isDublicate = true;
            }
        }
        return isDublicate;
    }

    public Boolean checkIfUserWithSameCardNumberExists(List<User> users, String inputCardNumber) {
        boolean isDublicate = false;
        for (User user : users) {
            if (user.getCardNumber().matches(inputCardNumber)) {
                isDublicate = true;
                System.out.println("User with this card number already exists ");
            }
        }
        return isDublicate;
    }

    List<User> findByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    //check if the registration input card credentials are valid and not still used, if they are, input is valid
    public Boolean validateCardInfoInput(List<User> users, String inputFirstName, String inputLastName, String inputCardNumber) {
        List<LibraryCard> cards = cardService.findAll();
        boolean validCredentials = false;
      

        Boolean cardNumberAlreadyUsed = checkIfUserWithSameCardNumberExists(users, inputCardNumber);
        Boolean areValidCredentialsOfACard= checkIfUserInfoMatchWithCardInfo(inputFirstName, inputLastName, inputCardNumber);

        
        if (cardNumberAlreadyUsed == false && areValidCredentialsOfACard == true) {
            
            validCredentials =true;
                         
        }
                   
System.out.println("Are credentials valid and not used : "+ validCredentials);


        return validCredentials;
    }

    //check if input is valid
    //if he does, return that the input card details are not valid
    List<User> findByCardNumber(String cardNumber) {

        return userRepository.findByCardNumber(cardNumber);
    }
    
     Page<User> findByCardNumber(String cardNumber, Pageable pageable) {

        return userRepository.findByCardNumber(cardNumber, pageable);
    }
     
      Page<User> findByUsername(String username, Pageable pageable) {

        return userRepository.findByUsername(username, pageable);
    }
     
    //check if the registration input  user names and card do exists

    public Boolean checkIfUserInfoMatchWithCardInfo(String inputFirstName, String inputLastName, String inputCardNumber) {
        List<LibraryCard> cards = cardService.findAll();
        boolean matches = false;

        //if the input data matches the a card records in the db, then the user is a valid client of the library and registartion can proceeds
        for (LibraryCard card : cards) {
            if (card.getCardNumber().matches(inputCardNumber) && card.getFirstName().matches(inputFirstName) && card.getLastName().matches(inputLastName)) {
                matches = true;
            }
        }
        return matches;
    }

}
