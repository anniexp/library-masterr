/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.securityTest;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author Lenovo
 */
@Controller
public class UserController {
    @Autowired
	private UserRepository userRepository;
	
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
               // String userRoleString = Rolee.valueOf("ROLE_USER");
                Boolean isEnabled = true;
		user.setPassword(encodedPassword);
                user.setRolee(userRole);
                user.setEnabled(isEnabled);

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
}
