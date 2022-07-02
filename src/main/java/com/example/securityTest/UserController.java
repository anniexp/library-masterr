/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.securityTest;

import static com.sun.corba.se.spi.presentation.rmi.StubAdapter.request;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
@Autowired
    private BookRepository bookRepository;
@Autowired
    private BookService bookService;
    
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
          user.setFirstName(inputFirstName);
          user.setLastName(inputLastName);
          user.setCardNumber(inputCardNumber);
          model.addAttribute("inputFirstName",inputFirstName);
           model.addAttribute("inputLastName",inputLastName);
           model.addAttribute("inputCardNumber",inputCardNumber);
           model.addAttribute("user",user);
            
            System.out.println(inputFirstName);
         System.out.println(inputLastName);
         System.out.println(inputCardNumber);
           System.out.println(user.getFirstName());
      // return "redirect:/registration-part-2";
       return "registration";
      }
      else{
          model.addAttribute("message","Input data is not valid!");
          
      return "registration-card";
      }
        

    }
    
   /* @GetMapping("/registration-part-2")
    public String showRegistrationForm(Model model,
            @ModelAttribute("inputFirstName") String inputFirstName,
           @ModelAttribute("inputLastName") String inputLastName,
           @ModelAttribute("inputCardNumber") String inputCardNumber ,
            @ModelAttribute("user") User user) {
        
        user.setCardNumber(inputCardNumber);
        user.setFirstName(inputFirstName);
        user.setLastName(inputLastName);
       
        model.addAttribute("inputFirstName",inputFirstName);
        model.addAttribute("inputLastName", inputLastName);
        model.addAttribute("inputCardNumber",   inputCardNumber);
         model.addAttribute("user",user);
        return "registration";

    }*/

    @PostMapping("/post_register")
    public String postRegister( Model model , @ModelAttribute("user") User user, BindingResult result) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        String userRole = Rolee.getROLE_USER().name();
        
        String inputFirstName=user.getFirstName();
        String inputLastName= user.getLastName();
         String inputCardNumber= user.getCardNumber();

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
          System.out.println("User first name is : " + inputFirstName);
         System.out.println(inputLastName);
         System.out.println(inputCardNumber);
        if("".equals(inputFirstName))
        {
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
        List<Book> wishlist = currUser.getWishlist();
         List<Report> userBorrows = reportService.findByBorrower(currUser);
         List<Report> userCurrentBorrows = reportService.findByKeyword(currUser.getUser_id().toString());
         List<Book> borrowRequests = currUser.getBorrowRequests();
         
        System.out.println("Numver of borrowed book by user : " + userBorrows.size());
        System.out.println("Number of users : " + users.size());
        System.out.println("Current employeeee is : " + currentEmployee);
         System.out.println("User wishlist : " + wishlist);
        
         
         model.addAttribute("userBorrows", userBorrows);
         model.addAttribute("userCurrentBorrows",userCurrentBorrows);
         model.addAttribute("wishlist",wishlist);
         model.addAttribute("borrowRequests",borrowRequests);
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

     
        Principal currentEmployee = UserController.getCurrentUser(request);
        String currUserUsername = currentEmployee.getName();
        User currUser = userService.findByUsername(currUserUsername).get(0);
        String newPass = "1111111111111";
        String newPass2 = "1111111111111";
        
        System.out.println("Current employeeee is : " + currentEmployee);
        model.addAttribute("user", currUser);
        model.addAttribute("newPass",  newPass);
        model.addAttribute("newPass2",   newPass2);
        

        return "change-user-password";
    }
    
    
    @PostMapping("/profile/update/pass")
    public String changePassword(Model model,
            @ModelAttribute("password") String password,
            @ModelAttribute("newPass") String newPass,
            @ModelAttribute("newPass2") String newPass2,
            HttpServletRequest request,
           // @RequestParam("newPass") String newPass,
             //@RequestParam("newPass2") String newPass2,
             
           
             
             
            User user, BindingResult result
    ) {
        
        if (result.hasErrors()) {
        return "/profile/edit/pass";
    }
        
      //  newPass= request.getParameter(newPass);
      //  newPass2= request.getParameter(newPass2);
       //  newPass = (String) model.getAttribute(newPass);
       //   newPass2 = (String) model.getAttribute(newPass2);
            System.out.println("Input New Password : " + newPass);
    System.out.println("Input New Password 2 : " + newPass2);
     
       
      //get current session user
        User currUser = userService.getCurrentLoggedUser();
        
          BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
       // String encodedPassword = passwordEncoder.encode(password);
        
        //see if input matches encoded password
        boolean isPasswordMatch = passwordEncoder.matches(password, currUser.getPassword());
		System.out.println("Password : " + password + "   isPasswordMatch    : " + isPasswordMatch);
                //see if the new password equals old one
               boolean isNewPasswordMatchOldPassword = passwordEncoder.matches(newPass, currUser.getPassword());    
                
   System.out.println("Input Old Password : " + password);
    System.out.println(" Session User Old Password : " +  currUser.getPassword());
    System.out.println("Input New Password : " + newPass);
    System.out.println("Input New Password 2 : " + newPass2);
		
        //see if input matches curr user encoded password
        if (isPasswordMatch == true) {
       //if new pass is not null
            if (!"".equals(newPass)) {
                   //if new pass does not match old password
                if (isNewPasswordMatchOldPassword == false) {
                    //if new password and new password 2 match
                    if (newPass2.equals(newPass)) {
                    //if all condidions are met, change user password
                        String encodedPassword = passwordEncoder.encode(newPass);
                        user.setPassword(encodedPassword);
                        user.setEmail(currUser.getEmail());
                        user.setPhoneNumber(currUser.getPhoneNumber());
                        user.setUserAddress(currUser.getUserAddress());
                        user.setUsername(currUser.getUsername());

                        user.setEnabled(currUser.isEnabled());
                        user.setUser_id(currUser.getUser_id());
                        user.setBorrowedBooks(currUser.getBorrowedBooks());
                        user.setRolee(currUser.getRolee());
                        user.setProfilePicture(currUser.getProfilePicture());
                        user.setPictureContent(currUser.getPictureContent());
                        user.setProfilePictureSize(currUser.getProfilePictureSize());
                        user.setFirstName(currUser.getFirstName());
                        user.setLastName(currUser.getLastName());
                        user.setCardNumber(currUser.getCardNumber());
                        
                        
        user.setBorrowRequests(currUser.getBorrowRequests());
        user.setWishlist(currUser.getWishlist());

                        userRepository.save(user);

                        model.addAttribute(user);
                        return "profilePage";

                    }
                    else {
                        model.addAttribute("mess", "New password 2 Does not match new password 1!");
                        return "change-user-password";
                    }

                } else {
                    model.addAttribute("mess", "New password cannot match old password!");
                    return "change-user-password";
                }

            } else {
                model.addAttribute("mess", "New password cannot be null!");
                return "change-user-password";
            }
        }
    else {
            model.addAttribute("mess", "Old Password does not match user password!");
            return "change-user-password";
        }

    }

   //update user info (email, addess, nickname, phone number)
    @PostMapping("/profile/update")
    public String updateUserProfileInfo(Model model, User user
           ,    
            //  @ModelAttribute("inputCardNumber") String inputCardNumber,
              // @ModelAttribute("inputFirstName") String inputFirstName,
              // @ModelAttribute("inputLastName") String inputLastName,

              BindingResult result
    ) {

        
       //get current session user
          User curruser =  userService.getCurrentLoggedUser();

        System.out.println("Current employeeee is : " + user);
       
         if (result.hasErrors()) {
            
            return "redirect:/profile/edit";
        }
         user.setFirstName(curruser.getFirstName());
         user.setLastName(curruser.getLastName());
         
  user.setCardNumber(curruser.getCardNumber());
  user.setEnabled(curruser.isEnabled());
  user.setUser_id(curruser.getUser_id());
 // user.setBorrowedBooks(user.getBorrowedBooks());
  user.setPassword(curruser.getPassword());
  user.setRolee(curruser.getRolee()
);
  user.setProfilePicture(curruser.getProfilePicture());
  user.setPictureContent(curruser.getPictureContent());
  user.setProfilePictureSize(curruser.getProfilePictureSize());
  
         user.setBorrowedBooks(curruser.getBorrowedBooks());
        user.setBorrowRequests(curruser.getBorrowRequests());
        user.setWishlist(curruser.getWishlist());
    
  userRepository.save(user);

  
  return "redirect:/profile";

    }
    
    
    
    
    
    //update user info (profile picture)
    @PostMapping("/profile/edit/upload")
 public String profilePictureUpload(@RequestParam("file") MultipartFile file,  Model model,
        
         User user) throws IOException {

   
       //get current session user
         user = userService.getCurrentLoggedUser();

        System.out.println("Current employeeee is : " + user);
     
   
//picture attributes
  
 
  if(file.getSize()>=1048576){
       model.addAttribute("failure", "Picture is too big!!!");
       return "profilePage";
  
          }
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
    
        user.setBorrowRequests(user.getBorrowRequests());
        user.setWishlist(user.getWishlist());

    
  userRepository.save(user);
  model.addAttribute(user);
  return "profilePage";
  
 }
 
 //loads profile picture, o
    @GetMapping("/profile/image")
    public void showIImage(@Param("username")String username, HttpServletResponse response,User user)
            throws ServletException, IOException {

    
         user = userService.findByUsername(username).get(0);

        System.out.println("Current employeeee is : " + user);
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif, image/pdf");
        response.getOutputStream().write(user.getPictureContent());
        response.getOutputStream().close();
    }

    
    
     @GetMapping("/users")
    public String index(Model model, @RequestParam(name = "searchUser", required = false) String searchUser,
               @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        
        List<User> users = null;
         Pageable paging = (Pageable) PageRequest.of(page, size);  
         Page<User> pageTuts;
        
        
         if (searchUser != null) {
              // pageTuts = userService.findByUsername(searchUser, paging);
                pageTuts = userService.findUserByIdOrUsernameOrCardNumber(searchUser, paging);
           // authors = authorRepository.findByKeyword(authorName);
        } else {
           // pageTuts = userRepository.findAll(paging);
           
            pageTuts = userRepository.findByRolee("ROLE_USER", paging);
        }
         
          users = pageTuts.getContent();     
        model.addAttribute("currentPage", pageTuts.getNumber());
        System.out.println("current page number is: " + pageTuts.getNumber());
        System.out.println("number of elements: " + pageTuts.getTotalElements());
        model.addAttribute("totalItems", pageTuts.getTotalElements());
         System.out.println("number of pages: " + pageTuts.getTotalPages());
        model.addAttribute("totalPages", pageTuts.getTotalPages());
        
        model.addAttribute("users", users);
        return "users";
    }
    
    
    
//ban-unban user
    @GetMapping("/users/ban/{userCard}")
    public String banUser(@PathVariable("userCard") String userCard, @Valid User user,
            BindingResult result, Model model) {
        
         System.out.println(" user card:" + userCard);
         
          user = userRepository.findByCardNumber(userCard).get(0);
             
        
        //make user not enabled to log into account
        //if user is banned, unban and if he is not banned, ban him
        System.out.println("user is : " + user.getUsername());
        System.out.println("user is enabled : " + user.isEnabled());
        if (user.isEnabled() ==true){
        user.setEnabled(false);}
        else {
         user.setEnabled(true);
        }
                System.out.println("user is enabled : " + user.isEnabled());

        user.setFirstName(user.getFirstName());
        user.setLastName(user.getLastName());       
        user.setCardNumber(user.getCardNumber());
        user.setUser_id(user.getUser_id());
        user.setBorrowedBooks(user.getBorrowedBooks());
        user.setPassword(user.getPassword());
        user.setRolee(user.getRolee()
);
        user.setProfilePicture(user.getProfilePicture());
        user.setPictureContent(user.getPictureContent());
        user.setProfilePictureSize(user.getProfilePictureSize());
        
        user.setEmail(user.getEmail());
        user.setPhoneNumber(user.getPhoneNumber());
        user.setUserAddress(user.getUserAddress());
        user.setUsername(user.getUsername());
         user.setBorrowedBooks(user.getBorrowedBooks());
        user.setBorrowRequests(user.getBorrowRequests());
        user.setWishlist(user.getWishlist());
        
        userRepository.save(user);
       
        return "redirect:/users";
    }
    
   
    
      @GetMapping("/user/wishlist")
    public String showWishList(Model model,
               User user,
                @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "16") int size
           // ,@RequestParam(defaultValue = "") String mess
       ) {
        
        //get session user
         
        User currUser = userService.getCurrentLoggedUser();
        
        //get his wishlist
         List<Book> wishlist = currUser.getWishlist();
         
         Pageable paging = (Pageable) PageRequest.of(page, size);  
         Page<Book> pageTuts;
         
   
         
        //create a pageable obj of the list of books in the wishlist of the user and get the content after that
        pageTuts = new PageImpl<>(wishlist);
        wishlist = pageTuts.getContent(); 
  
        
        System.out.println("User wishlist : " + wishlist);
                
                
        
        model.addAttribute("currentPage", pageTuts.getNumber());
        System.out.println("current page number is: " + pageTuts.getNumber());
        System.out.println("number of elements: " + pageTuts.getTotalElements());
        model.addAttribute("totalItems", pageTuts.getTotalElements());
         System.out.println("number of pages: " + pageTuts.getTotalPages());
        model.addAttribute("totalPages", pageTuts.getTotalPages());
        
          model.addAttribute("books", wishlist);
           model.addAttribute("user", currUser);
           model.addAttribute("subtitle", currUser.getFirstName() + "'s Wishlist");
        //model.addAttribute("mess",mess);
       
        return "books";
    }
    
     @PostMapping("/books/addToWishlist/{bookId}")
    public String addBookToWishlist(@PathVariable("bookId") long bookId,
            @Valid User user, BindingResult result, Model model, 
            RedirectAttributes attributes) {

       Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + bookId));
        String mess =null;
        User currUser = userService.getCurrentLoggedUser();
        //add the book to his wishlist 
       List<Book> newWishList =  bookService.addToWishList(book);
        
       if(newWishList.size()!=currUser.getWishlist().size()){
           
        mess =  "Book already exists in wishlist";
       }
       else{
           
         mess=  "Book added successfully into wishlist";
       }
       
        user.setWishlist(newWishList);
          user.setUser_id(currUser.getUser_id());
          user.setUsername(currUser.getUsername());
          user.setPassword(currUser.getPassword());
        user.setEmail(currUser.getEmail());
        user.setPhoneNumber(currUser.getPhoneNumber());
        
        user.setFirstName(currUser.getFirstName());
        user.setLastName(currUser.getLastName());    
         user.setCardNumber(currUser.getCardNumber());
         
         user.setRolee(currUser.getRolee());
         user.setEnabled(currUser.isEnabled());
                
        user.setProfilePicture(currUser.getProfilePicture());
        user.setPictureContent(currUser.getPictureContent());
        user.setProfilePictureSize(currUser.getProfilePictureSize());
        
        user.setUserAddress(currUser.getUserAddress()); 
        user.setBorrowedBooks(currUser.getBorrowedBooks());
        
        user.setBorrowRequests(currUser.getBorrowRequests());
        
            
        userRepository.save(user);
        model.addAttribute("user",user);
        
        model.addAttribute("mess",mess);
               
        return "redirect:/user/wishlist";
    }
    
     @PostMapping("/books/removeFromWishlist/{bookId}")
    public String removeBookFromWishlist(@PathVariable("bookId") long bookId,
            @Valid User user, BindingResult result, Model model, 
            RedirectAttributes attributes) {

       Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + bookId));
        String mess =null;
        User currUser = userService.getCurrentLoggedUser();
        //add the book to his wishlist 
       List<Book> newWishList =  bookService.removeBookFromWishList(book);
        
       if(newWishList.size()!=currUser.getWishlist().size()){
           
        mess =  "Book removed successfully from wishlist";
       }
       else{
           
         mess=  "Book wasn't in wishlist";
       }
       
        user.setWishlist(newWishList);
          user.setUser_id(currUser.getUser_id());
          user.setUsername(currUser.getUsername());
          user.setPassword(currUser.getPassword());
        user.setEmail(currUser.getEmail());
        user.setPhoneNumber(currUser.getPhoneNumber());
        
        user.setFirstName(currUser.getFirstName());
        user.setLastName(currUser.getLastName());    
         user.setCardNumber(currUser.getCardNumber());
         
         user.setRolee(currUser.getRolee());
         user.setEnabled(currUser.isEnabled());
                
        user.setProfilePicture(currUser.getProfilePicture());
        user.setPictureContent(currUser.getPictureContent());
        user.setProfilePictureSize(currUser.getProfilePictureSize());
        
        user.setUserAddress(currUser.getUserAddress()); 
        user.setBorrowedBooks(currUser.getBorrowedBooks());
        user.setBorrowRequests(currUser.getBorrowRequests());
   
        userRepository.save(user);
        model.addAttribute("user",user);
        
        model.addAttribute("mess",mess);
               
        return "redirect:/user/wishlist";
    }



 @PostMapping("/books/createRequestToBorrow/{bookId}")
    public String createRequestToBorrowBook(@PathVariable("bookId") long bookId,
            @Valid User user, BindingResult result, Model model, 
            RedirectAttributes attributes) {

       Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + bookId));
        String mess =null;
        User currUser = userService.getCurrentLoggedUser();
        
       boolean isApproved = false;
        //add the book to his wishlist 
       List<Book> requestToB =  bookService.createABorrowRequest(book);
        
       if(requestToB.size()!=currUser.getBorrowRequests().size()){
           
        mess =  "Book already is requested";
       }
       else{
           
         mess=  "Book requested successfully";
       }
       /*
        boolean[] appArray = user.getBorrowApproved();
        int size = appArray.length;
         boolean[] newAppArray = new boolean[size];
         newAppArray[size] = isApproved;
         */
        
        /* List arrList = new ArrayList( Arrays.asList(appArray));
         arrList.add(isApproved);
         appArray = arrList.toArray(appArray);*/
        
      
      
       user.setBorrowRequests(requestToB);
       
        user.setWishlist(currUser.getWishlist());
          user.setUser_id(currUser.getUser_id());
          user.setUsername(currUser.getUsername());
          user.setPassword(currUser.getPassword());
        user.setEmail(currUser.getEmail());
        user.setPhoneNumber(currUser.getPhoneNumber());
        
        user.setFirstName(currUser.getFirstName());
        user.setLastName(currUser.getLastName());    
         user.setCardNumber(currUser.getCardNumber());
         
         user.setRolee(currUser.getRolee());
         user.setEnabled(currUser.isEnabled());
                
        user.setProfilePicture(currUser.getProfilePicture());
        user.setPictureContent(currUser.getPictureContent());
        user.setProfilePictureSize(currUser.getProfilePictureSize());
        
        user.setUserAddress(currUser.getUserAddress()); 
        user.setBorrowedBooks(currUser.getBorrowedBooks());
        user.setBorrowApproved(false);
        
        
            
        userRepository.save(user);
        model.addAttribute("user",user);
        
        model.addAttribute("mess",mess);
               
        return "redirect:/profile";
    }
    
    
    
    
    
    //show to the staff what are the current pending requests, so he can create a report and lend the book to the user
    @GetMapping("/pending-borrow-requests")
    public String showPendingRequests(Model model, @RequestParam(name = "searchUser", required = false) String searchUser,
               @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        
         List<User> users = null;
         Pageable paging = (Pageable) PageRequest.of(page, size);  
         Page<Book> pageTuts;
        List<Book> allBorrowRequests = new ArrayList<>();
        
       
        
       List<User> usersToList = new ArrayList<>();
           
            users = userRepository.findAll();
                        System.out.println("Number of all users " + users.size());
                        
                       //  users = userRepository.findAll();
                        System.out.println("Number of all users " + users.size());

            for(User user : users){
            List <Book> userBorrowRequests = user.getBorrowRequests();
            if(!userBorrowRequests.isEmpty()){
                
                usersToList.add(user);
                   System.out.println("Number of users with borrow requests after add = " + usersToList.size());
            }}
                            

            for(User user : users){
            List <Book> userBorrowRequests = user.getBorrowRequests();
            System.out.println("Number of borrow requests before add = " + allBorrowRequests.size());
            allBorrowRequests.addAll(userBorrowRequests);
            System.out.println("Number of borrow requests after add = " + allBorrowRequests.size());
            }
          //  pageTuts =new PageImpl<>(allBorrowRequests);
        // Page<Book>alllBorrowRequests = userRepository.findAllBorrowRequests(paging);
        // pageTuts = alllBorrowRequests;
        //allBorrowRequests = pageTuts.getContent();
      /*  List<Book> allllBorrowRequests = userRepository.findAllBorrowRequests();
        
        for(Book req:allllBorrowRequests ){
            
        System.out.println(" borrow request= " + req.toString());
        }
      //  System.out.println(" borrow requests= " + allllBorrowRequests);
        
              */  
        
       // model.addAttribute("currentPage", pageTuts.getNumber());
        //System.out.println("current page number is: " + pageTuts.getNumber());
       // System.out.println("number of elements: " + pageTuts.getTotalElements());
       // model.addAttribute("totalItems", pageTuts.getTotalElements());
       //  System.out.println("number of pages: " + pageTuts.getTotalPages());
       // model.addAttribute("totalPages", pageTuts.getTotalPages());
        
         // model.addAttribute("allBorrowRequests", allllBorrowRequests);
  
       model.addAttribute("borrowRequests", allBorrowRequests);
        model.addAttribute("users", usersToList);
        return "pending-borrow-requests";
    }
    

}