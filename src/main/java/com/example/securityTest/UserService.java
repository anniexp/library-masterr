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

    
}
