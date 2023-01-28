package com.example.securityTest;

import java.security.Principal;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author Lenovo
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final LibraryCardService cardService;
    private final HttpServletRequest request;

    @Autowired
    public UserService(UserRepository userRepository, LibraryCardService cardService, HttpServletRequest request) {
        this.userRepository = userRepository;
        this.cardService = cardService;
        this.request = request;
    }

    User getCurrentLoggedUser() {
        Principal currentEmployee = UserController.getCurrentUser(request);
        String currUserUsername = currentEmployee.getName();
        return findByUsername(currUserUsername).get(0);
    }

    public Boolean checkIfUserWithUsernameExists(List<User> users, String inputUsername) {
        boolean isDublicate = false;
        for (User user : users) {
            if (user.getUsername().matches(inputUsername)) {
                isDublicate = true;
                break;
            }
        }

        return isDublicate;
    }

    public Boolean checkIfUserWithSameEmailExists(List<User> users, String inputEmail) {
        boolean isDublicate = false;
        for (User user : users) {
            if (user.getEmail().matches(inputEmail)) {
                isDublicate = true;
                break;
            }
        }

        return isDublicate;
    }

    public Boolean checkIfUserWithSameCardNumberExists(List<User> users, String inputCardNumber) {
        boolean isDublicate = false;
        for (User user : users) {
            if (user.getCardNumber().matches(inputCardNumber)) {
                isDublicate = true;
                break;
            }
        }
        return isDublicate;
    }

    List<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    //check if the registration input card credentials are valid and not still used, if they are, input is valid
    public Boolean validateCardInfoInput(List<User> users, String inputFirstName, String inputLastName, String inputCardNumber) {
        // List<LibraryCard> cards = cardService.findAll();
        boolean validCredentials = false;
        Boolean cardNumberAlreadyUsed = checkIfUserWithSameCardNumberExists(users, inputCardNumber);
        Boolean areValidCredentialsOfACard = checkIfUserInfoMatchWithCardInfo(inputFirstName, inputLastName, inputCardNumber);

        if (!cardNumberAlreadyUsed && areValidCredentialsOfACard) {
            validCredentials = true;
        }

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

    Page<User> findUserByIdOrUsernameOrCardNumber(String keyword, Pageable pageable) {
        return userRepository.findUserByIdOrUsernameOrCardNumber(keyword, pageable);
    }

    //check if the registration input  user names and card do exists

    public Boolean checkIfUserInfoMatchWithCardInfo(String inputFirstName, String inputLastName, String inputCardNumber) {
        List<LibraryCard> cards = cardService.findAll();
        boolean matches = false;

        //if the input data matches the a card records in the db, then the user is a valid client of the library and registartion can proceeds
        for (LibraryCard card : cards) {
            if (card.getCardNumber().matches(inputCardNumber) && card.getFirstName().matches(inputFirstName) && card.getLastName().matches(inputLastName)) {
                matches = true;
                break;
            }
        }

        return matches;
    }
}
