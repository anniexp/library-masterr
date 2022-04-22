/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.securityTest;

import static com.sun.corba.se.spi.presentation.rmi.StubAdapter.request;
import java.security.Principal;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

/**
 *
 * @author Lenovo
 */
@ControllerAdvice
@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    
     @Autowired
    private ReportService reportService;
     
    @Autowired
    private HttpServletRequest request;

    
    @GetMapping("/registration-part-1")
    public String showRegistrationFirstForm(Model model) {
        model.addAttribute("user", new User());
   
        return "registration-card";

    }
    
     @PostMapping("/register")
    public String showRegistrationSecondForm(Model model,@ModelAttribute("user") User user ) {
        
       String inputFirstName = user.getFirstName();
       String inputLastName = user.getLastName();
       String inputCardNumber = user.getCardNumber();
       
      Boolean correctCredentials = userService.checkIfUserInfoMatchWithCardInfo(inputFirstName, inputLastName, inputCardNumber);
      
      
      if (correctCredentials){
          model.addAttribute("inputFirstName",inputFirstName);
           model.addAttribute("inputLastName",inputLastName);
            model.addAttribute("inputCardNumber",inputCardNumber);
       return "redirect:/registration-part-2";
      }
      else{
          model.addAttribute("message","Data does not match card info!");
          
      return "registration-card";
      }
        

    }
    @GetMapping("/registration-part-2")
    public String showRegistrationForm(Model model,@ModelAttribute("inputFirstName") String inputFirstName,@ModelAttribute("inputLastName") String inputLastName,
            @ModelAttribute("inputCardNumber") String inputCardNumber ,@ModelAttribute("user") User user) {
        user.setCardNumber(inputCardNumber);
        user.setFirstName(inputFirstName);
        user.setLastName(inputLastName);
        return "registration";

    }

    @PostMapping("/post_register")
    public String postRegister(User user, Model model , @ModelAttribute("inputFirstName") String inputFirstName,@ModelAttribute("inputLastName") String inputLastName,
            @ModelAttribute("inputCardNumber") String inputCardNumber ) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        String userRole = Rolee.getROLE_USER().name();

        //if there exists an user with the input username, return the view
        if (userService.checkIfUserExists(user.getUsername()) == true) {

            return "registration";
        }

        //if there exists an user with the input email address, return the view
        if (userService.checkIfUserWithSameEmailExists(user.getEmail()) == true) {
            return "registration";
        }
        // String userRoleString = Rolee.valueOf("ROLE_USER");
        Boolean isEnabled = true;
        user.setPassword(encodedPassword);
        user.setRolee(userRole);
        user.setEnabled(isEnabled);
        
        user.setCardNumber(inputCardNumber);
        user.setFirstName(inputFirstName);
        user.setLastName(inputLastName);

        // userService.checkIfUserExists(user.getUsername());
        userRepository.save(user);

        return "successful_registration";
    }

    @GetMapping("/login")
    public String viewLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String viewHomePage() {

        return "redirect:/";
    }

    //   @RequestMapping(value = "/username", method = RequestMethod.GET)    
    public static Principal getCurrentUser(HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();
        System.out.println("Current user has name : " + principal.getName());
        return principal;
    }

    @GetMapping("/profile")
    public String showUserProfile(Model model
    ) {

        List<User> users = userRepository.findAll();
        Principal currentEmployee = UserController.getCurrentUser(request);
        String currUserUsername = currentEmployee.getName();
        User currUser = userService.findByUsername(currUserUsername).get(0);

        System.out.println("Number of users : " + users.size());
        System.out.println("Current employeeee is : " + currentEmployee);
        
         List<Report> userBorrows = reportService.findByBorrower(currUser);
         List<Report> userCurrentBorrows = reportService.findByKeyword(currUser.getUser_id().toString());
         
         
        System.out.println("Numver of borrowed book by user : " + userBorrows.size());
         model.addAttribute("userBorrows", userBorrows);
         model.addAttribute("userCurrentBorrows",userCurrentBorrows);
        model.addAttribute("user", currUser);

        return "profilePage";
    }

    @GetMapping("/profile/edit")
    public String showUserProfileEditForm(Model model
    ) {

        List<User> users = userRepository.findAll();
        Principal currentEmployee = UserController.getCurrentUser(request);
        String currUserUsername = currentEmployee.getName();
        User currUser = userService.findByUsername(currUserUsername).get(0);

        System.out.println("Current employeeee is : " + currentEmployee);
        model.addAttribute("user", currUser);

        return "update-user-profile";
    }
    
    
    
    
    @GetMapping("/profile/edit/pass")
    public String showUserChangePasswordForm(Model model
    ) {

        List<User> users = userRepository.findAll();
        Principal currentEmployee = UserController.getCurrentUser(request);
        String currUserUsername = currentEmployee.getName();
        User currUser = userService.findByUsername(currUserUsername).get(0);

        System.out.println("Current employeeee is : " + currentEmployee);
        model.addAttribute("user", currUser);

        return "change-user-password";
    }
    
 
}
