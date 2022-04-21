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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

/**
 *
 * @author Lenovo
 */
@Controller
public class UserController {
    @Autowired
	private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private HttpServletRequest request;
	
	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("user", new User());

		return "registration";
	
        }
        
	@PostMapping("/post_register")
	public String postRegister(User user) {
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
                
               // userService.checkIfUserExists(user.getUsername());

                
                

		userRepository.save(user);

		return "successful_registration";
	}
        
          @GetMapping("/login")
	    public String viewLoginPage() {	         
	        return "login";
	    }
            
            @PostMapping("/login")
            public String viewHomePage(){
              
            return "redirect:/";
            }
            
     //   @RequestMapping(value = "/username", method = RequestMethod.GET)    
        public static  Principal  getCurrentUser(HttpServletRequest request){
            
            Principal principal = request.getUserPrincipal();
            System.out.println("Current user has name : " + principal.getName());
        return principal;
        }
        
        @GetMapping("/profile")
                 public String showUserProfile(Model model
      ){
 
         List<User> users = userRepository.findAll();
        Principal currentEmployee = UserController.getCurrentUser(request);
            String currUserUsername = currentEmployee.getName();
            User currUser = userService.findByUsername(currUserUsername).get(0);
      //  User currentUserEmployee= (User) currentEmployee;
      
      
        //if it doesnt work, then to build it with a constructor, if that does not work, 
        //then firstly get the name of the cuurent user, then loop all users, if the
        //name equals, then this is the cuurent user 
        //User currentUserEmployee= new User(currentEmployee., String password,
        //String username, String rolee, boolean enabled);
      
       
        System.out.println("Number of users : " + users.size());
        System.out.println("Current employeeee is : " + currentEmployee);
        
         model.addAttribute("user", currUser);
         
         
        

        return "profilePage";
        
        
                 }

}
