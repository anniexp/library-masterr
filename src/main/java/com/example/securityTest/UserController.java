/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.securityTest;

import static com.sun.corba.se.spi.presentation.rmi.StubAdapter.request;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
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
        List<User> users = userRepository.findAll();
       String inputFirstName = user.getFirstName();
       String inputLastName = user.getLastName();
       String inputCardNumber = user.getCardNumber();
       
     // Boolean correctCredentials = userService.checkIfUserInfoMatchWithCardInfo(inputFirstName, inputLastName, inputCardNumber);
       Boolean areValidCredentials = userService.validateCardInfoInput( users,inputFirstName,  inputLastName, inputCardNumber);
      
      if (areValidCredentials == true){
          model.addAttribute("inputFirstName",inputFirstName);
           model.addAttribute("inputLastName",inputLastName);
            model.addAttribute("inputCardNumber",inputCardNumber);
            
       return "redirect:/registration-part-2";
      }
      else{
          model.addAttribute("message","Input data is not valid!");
          
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
        
        List<User> users = userRepository.findAll();

        //if there exists an user with the input username, return the view
        if (userService.checkIfUserWithUsernameExists(users,user.getUsername()) == true) {
            model.addAttribute("message","Username already taken!");
            model.addAttribute("inputFirstName",inputFirstName);
            model.addAttribute("inputLastName",inputLastName);
            model.addAttribute("inputCardNumber",inputCardNumber);
            
            return "registration";
        }

        //if there exists an user with the input email address, return the view
        if (userService.checkIfUserWithSameEmailExists(users, user.getEmail()) == true) {
            
            model.addAttribute("message","User with this email already exists!");
            model.addAttribute("inputFirstName",inputFirstName);
            model.addAttribute("inputLastName",inputLastName);
            model.addAttribute("inputCardNumber",inputCardNumber);
            
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

        
        Principal currentEmployee = UserController.getCurrentUser(request);
        String currUserUsername = currentEmployee.getName();
        User currUser = userService.findByUsername(currUserUsername).get(0);

        System.out.println("Current employeeee is : " + currentEmployee);
        
        model.addAttribute("inputCardNumber",currUser.getCardNumber());
        model.addAttribute("inputFirstName",currUser.getFirstName());
        model.addAttribute("inputLastName",currUser.getLastName());
        model.addAttribute("pass",currUser.getPassword());

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
    
    @PostMapping("/profile/update/pass")
    public String changePassword(Model model,
            @ModelAttribute("password") String password,
            @ModelAttribute("newPass") String newPass,
            @ModelAttribute("newPass2") String newPass2,
            User user
    ) {
        if (password.equals(user.getPassword())) {
            if (newPass.equals(newPass2)) {
                user.setPassword(newPass);
                  user.setEmail(user.getEmail());
         user.setPhoneNumber(user.getPhoneNumber());
         user.setUserAddress(user.getUserAddress());
         user.setUsername(user.getUsername());
  
  user.setEnabled(user.isEnabled());
  user.setUser_id(user.getUser_id());
  user.setBorrowedBooks(user.getBorrowedBooks());
  user.setRolee(user.getRolee());
user.setProfilePicture(user.getProfilePicture());
  user.setPictureContent(user.getPictureContent());
  user.setProfilePictureSize(user.getProfilePictureSize());
    
  userRepository.save(user);

                 model.addAttribute(user);
                 return "profilePage";
                
                

            }
        }
        else{
            model.addAttribute("mess","New password Does not match");
       return "change-user-password";
        }
        model.addAttribute("mess","Old Password does not match");
return "change-user-password";
       
    }
    
   
    @PostMapping("/profile/update")
    public String updateUserProfileInfo(Model model, User user,    
              @ModelAttribute("inputCardNumber") String inputCardNumber,
               @ModelAttribute("inputFirstName") String inputFirstName,
               @ModelAttribute("inputLastName") String inputLastName,

              BindingResult result
    ) {

        Principal currentEmployee = UserController.getCurrentUser(request);
        String currUserUsername = currentEmployee.getName();
         User curruser = userService.findByUsername(currUserUsername).get(0);
         

        System.out.println("Current employeeee is : " + user);
       
         if (result.hasErrors()) {
            
            return "redirect:/profile/edit";
        }
         user.setFirstName(inputFirstName);
         user.setLastName(inputLastName);
         
  user.setCardNumber(inputCardNumber);
  user.setEnabled(curruser.isEnabled());
  user.setUser_id(curruser.getUser_id());
 // user.setBorrowedBooks(user.getBorrowedBooks());
  user.setPassword(curruser.getPassword());
  user.setRolee(Rolee.getROLE_USER().name()
);
  user.setProfilePicture(curruser.getProfilePicture());
  user.setPictureContent(curruser.getPictureContent());
  user.setProfilePictureSize(curruser.getProfilePictureSize());
    
  userRepository.save(user);

  
  return "redirect:/profile";

    }
    
    
    
    
    
    
    @PostMapping("/profile/edit/upload")
 public String profilePictureUpload(@RequestParam("file") MultipartFile file,  Model model,
        
         User user) throws IOException {

       List<User> users = userRepository.findAll();
        Principal currentEmployee = UserController.getCurrentUser(request);
        String currUserUsername = currentEmployee.getName();
         user = userService.findByUsername(currUserUsername).get(0);

        System.out.println("Current employeeee is : " + currentEmployee);
     
   
//picture attributes
  String fileName = file.getOriginalFilename();
  user.setProfilePicture(fileName);
  user.setPictureContent(file.getBytes());
  user.setProfilePictureSize(file.getSize());
  
  model.addAttribute("success", "File Uploaded Successfully!!!");
  model.addAttribute("profilePicture", user.getProfilePicture());
  model.addAttribute("content",user.getPictureContent());
  model.addAttribute("size",user.getProfilePictureSize() );

  System.out.println("Image is uploaded");
  model.addAttribute("success", "File Uploaded Successfully!!!");
  
  System.out.println( "User email : " + user.getEmail());
  
  user.setEmail(user.getEmail());
  
  user.setUsername(user.getUsername());
    user.setUserAddress(user.getUserAddress());
      user.setPhoneNumber(user.getPhoneNumber());

    
  user.setCardNumber(user.getCardNumber());
  user.setFirstName(user.getFirstName());
  user.setLastName(user.getLastName());
  user.setEnabled(user.isEnabled());
  user.setUser_id(user.getUser_id());
  user.setBorrowedBooks(user.getBorrowedBooks());
  user.setPassword(user.getPassword());
  user.setRolee(user.getRolee());

    
  userRepository.save(user);
  model.addAttribute(user);
  return "profilePage";
  
 }
 
 
    @GetMapping("/profile/image")
    public void showIImage(@Param("username")String username, HttpServletResponse response,User user)
            throws ServletException, IOException {

        
        //Principal currentEmployee = UserController.getCurrentUser(request);
      //  String currUserUsername = currentEmployee.getName();
         user = userService.findByUsername(username).get(0);

        System.out.println("Current employeeee is : " + user);
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif, image/pdf");
        response.getOutputStream().write(user.getPictureContent());
        response.getOutputStream().close();
    }

    
  
}
