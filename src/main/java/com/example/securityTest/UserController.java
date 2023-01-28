package com.example.securityTest;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Lenovo
 */
@ControllerAdvice
@Controller
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;
    private final ReportService reportService;
    private final HttpServletRequest request;
    private final BookRepository bookRepository;
    private final BookService bookService;

    @Autowired
    public UserController(UserRepository userRepository, UserService userService, ReportService reportService, HttpServletRequest request, BookRepository bookRepository, BookService bookService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.reportService = reportService;
        this.request = request;
        this.bookRepository = bookRepository;
        this.bookService = bookService;
    }

    @GetMapping("/registration-part-1")
    public String showRegistrationFirstForm(Model model) {
        model.addAttribute("user", new User());

        return "registration-card";
    }

    @PostMapping("/register")
    public String showRegistrationSecondForm(Model model, @ModelAttribute("user") User user) {
        List<User> users = userRepository.findAll();
        String inputFirstName = user.getFirstName();
        String inputLastName = user.getLastName();
        String inputCardNumber = user.getCardNumber();

        Boolean areValidCredentials = userService.validateCardInfoInput(users, inputFirstName, inputLastName, inputCardNumber);

        if (areValidCredentials) {
            user.setFirstName(inputFirstName);
            user.setLastName(inputLastName);
            user.setCardNumber(inputCardNumber);
            model.addAttribute("inputFirstName", inputFirstName);
            model.addAttribute("inputLastName", inputLastName);
            model.addAttribute("inputCardNumber", inputCardNumber);
            model.addAttribute("user", user);

            return "registration";
        } else {
            model.addAttribute("message", "Input data is not valid!");

            return "registration-card";
        }
    }


    @PostMapping("/post_register")
    public String postRegister(Model model, @ModelAttribute("user") User user, BindingResult result) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        String userRole = Rolee.getROLE_USER().name();
        String inputFirstName = user.getFirstName();
        String inputLastName = user.getLastName();
        String inputCardNumber = user.getCardNumber();

        List<User> users = userRepository.findAll();

        if (userService.checkIfUserWithUsernameExists(users, user.getUsername())) {
            model.addAttribute("message", "Username already taken!");
            model.addAttribute("inputFirstName", inputFirstName);
            model.addAttribute("inputLastName", inputLastName);
            model.addAttribute("inputCardNumber", inputCardNumber);

            return "registration";
        }

        //if there exists an user with the input email address, return the view
        if (userService.checkIfUserWithSameEmailExists(users, user.getEmail())) {
            model.addAttribute("message", "User with this email already exists!");
            model.addAttribute("inputFirstName", inputFirstName);
            model.addAttribute("inputLastName", inputLastName);
            model.addAttribute("inputCardNumber", inputCardNumber);

            return "registration";
        }
        if ("".equals(inputFirstName)) {
            return "registration";
        }
        Boolean isEnabled = true;
        user.setPassword(encodedPassword);
        user.setRolee(userRole);
        user.setEnabled(isEnabled);

        user.setCardNumber(inputCardNumber);
        user.setFirstName(inputFirstName);
        user.setLastName(inputLastName);

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

    public static Principal getCurrentUser(HttpServletRequest request) {
        return request.getUserPrincipal();
    }

    @GetMapping("/profile")
    public String showUserProfile(Model model) {
        Principal currentEmployee = UserController.getCurrentUser(request);
        String currUserUsername = currentEmployee.getName();
        User currUser = userService.findByUsername(currUserUsername).get(0);
        List<Book> wishlist = currUser.getWishlist();
        List<Report> userBorrows = reportService.findByBorrower(currUser);
        List<Report> userCurrentBorrows = reportService.findByKeyword(currUser.getUser_id().toString());
        List<Book> borrowRequests = currUser.getBorrowRequests();

        model.addAttribute("userBorrows", userBorrows);
        model.addAttribute("userCurrentBorrows", userCurrentBorrows);
        model.addAttribute("wishlist", wishlist);
        model.addAttribute("borrowRequests", borrowRequests);
        model.addAttribute("user", currUser);

        return "profilePage";
    }

    @GetMapping("/profile/edit")
    public String showUserProfileEditForm(Model model) {
        Principal currentEmployee = UserController.getCurrentUser(request);
        String currUserUsername = currentEmployee.getName();
        User currUser = userService.findByUsername(currUserUsername).get(0);

        model.addAttribute("inputCardNumber", currUser.getCardNumber());
        model.addAttribute("inputFirstName", currUser.getFirstName());
        model.addAttribute("inputLastName", currUser.getLastName());
        model.addAttribute("pass", currUser.getPassword());
        model.addAttribute("user", currUser);

        return "update-user-profile";
    }

    @GetMapping("/profile/edit/pass")
    public String showUserChangePasswordForm(Model model) {
        Principal currentEmployee = UserController.getCurrentUser(request);
        String currUserUsername = currentEmployee.getName();
        User currUser = userService.findByUsername(currUserUsername).get(0);
        String newPass = "1111111111111";
        String newPass2 = "1111111111111";

        model.addAttribute("user", currUser);
        model.addAttribute("newPass", newPass);
        model.addAttribute("newPass2", newPass2);

        return "change-user-password";
    }

    @PostMapping("/profile/update/pass")
    public String changePassword(Model model,
                                 @ModelAttribute("password") String password,
                                 @ModelAttribute("newPass") String newPass,
                                 @ModelAttribute("newPass2") String newPass2,
                                 User user, BindingResult result) {
        if (result.hasErrors()) {

            return "/profile/edit/pass";
        }
        //get current session user
        User currUser = userService.getCurrentLoggedUser();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        //see if input matches encoded password
        boolean isPasswordMatch = passwordEncoder.matches(password, currUser.getPassword());
        //see if the new password equals old one
        boolean isNewPasswordMatchOldPassword = passwordEncoder.matches(newPass, currUser.getPassword());

        //see if input matches curr user encoded password
        if (isPasswordMatch) {
            //if new pass is not null
            if (!"".equals(newPass)) {
                //if new pass does not match old password
                if (!isNewPasswordMatchOldPassword) {
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

                    } else {
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
        } else {
            model.addAttribute("mess", "Old Password does not match user password!");
            return "change-user-password";
        }
    }

    //update user info (email, addess, nickname, phone number)
    @PostMapping("/profile/update")
    public String updateUserProfileInfo(Model model, User user
            , BindingResult result) {
        //get current session user
        User curruser = userService.getCurrentLoggedUser();
        if (result.hasErrors()) {

            return "redirect:/profile/edit";
        }
        user.setFirstName(curruser.getFirstName());
        user.setLastName(curruser.getLastName());

        user.setCardNumber(curruser.getCardNumber());
        user.setEnabled(curruser.isEnabled());
        user.setUser_id(curruser.getUser_id());
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
    public String profilePictureUpload(@RequestParam("file") MultipartFile file, Model model,
                                       User user) throws IOException {
        //get current session user
        user = userService.getCurrentLoggedUser();
        //picture attributes
        if (file.getSize() >= 1048576) {
            model.addAttribute("failure", "Picture is too big!!!");

            return "profilePage";
        }
        String fileName = file.getOriginalFilename();
        user.setProfilePicture(fileName);
        user.setPictureContent(file.getBytes());
        user.setProfilePictureSize(file.getSize());

        model.addAttribute("success", "File Uploaded Successfully!!!");
        model.addAttribute("profilePicture", user.getProfilePicture());
        model.addAttribute("content", user.getPictureContent());
        model.addAttribute("size", user.getProfilePictureSize());
        model.addAttribute("success", "File Uploaded Successfully!!!");

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
    public void showIImage(@Param("username") String username, HttpServletResponse response) throws IOException {
        User user = userService.findByUsername(username).get(0);
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif, image/pdf");
        response.getOutputStream().write(user.getPictureContent());
        response.getOutputStream().close();
    }

    @GetMapping("/users")
    public String index(Model model, @RequestParam(name = "searchUser", required = false) String searchUser,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
        List<User> users;
        Pageable paging = PageRequest.of(page, size);
        Page<User> pageTuts;

        if (searchUser != null) {
            pageTuts = userService.findUserByIdOrUsernameOrCardNumber(searchUser, paging);
        } else {
            pageTuts = userRepository.findByRolee("ROLE_USER", paging);
        }

        users = pageTuts.getContent();
        model.addAttribute("currentPage", pageTuts.getNumber());
        model.addAttribute("totalItems", pageTuts.getTotalElements());
        model.addAttribute("totalPages", pageTuts.getTotalPages());
        model.addAttribute("users", users);

        return "users";
    }

    //ban-unban user
    @GetMapping("/users/ban/{userCard}")
    public String banUser(@PathVariable("userCard") String userCard) {
        User user = userRepository.findByCardNumber(userCard).get(0);
        //make user not enabled to log into account
        //if user is banned, unban and if he is not banned, ban him
        user.setEnabled(!user.isEnabled());

        user.setFirstName(user.getFirstName());
        user.setLastName(user.getLastName());
        user.setCardNumber(user.getCardNumber());
        user.setUser_id(user.getUser_id());
        user.setBorrowedBooks(user.getBorrowedBooks());
        user.setPassword(user.getPassword());
        user.setRolee(user.getRolee());
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
    public String showWishList(Model model) {
        //get session user
        User currUser = userService.getCurrentLoggedUser();
        //get his wishlist
        List<Book> wishlist = currUser.getWishlist();
        Page<Book> pageTuts;
        //create a pageable obj of the list of books in the wishlist of the user and get the content after that
        pageTuts = new PageImpl<>(wishlist);
        wishlist = pageTuts.getContent();
        model.addAttribute("currentPage", pageTuts.getNumber());
        model.addAttribute("totalItems", pageTuts.getTotalElements());
        model.addAttribute("totalPages", pageTuts.getTotalPages());
        model.addAttribute("books", wishlist);
        model.addAttribute("user", currUser);
        model.addAttribute("subtitle", currUser.getFirstName() + "'s Wishlist");

        return "books";
    }

    @PostMapping("/books/addToWishlist/{bookId}")
    public String addBookToWishlist(@PathVariable("bookId") long bookId, @Valid User user, Model model) {
        Book book = bookRepository.findById(bookId).orElseThrow(() ->
                new IllegalArgumentException("Invalid book Id:" + bookId));
        String mess;
        User currUser = userService.getCurrentLoggedUser();
        //add the book to his wishlist 
        List<Book> newWishList = bookService.addToWishList(book);
        if (newWishList.size() != currUser.getWishlist().size()) {
            mess = "Book already exists in wishlist";
        } else {
            mess = "Book added successfully into wishlist";
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
        model.addAttribute("user", user);
        model.addAttribute("mess", mess);

        return "redirect:/user/wishlist";
    }

    @PostMapping("/books/removeFromWishlist/{bookId}")
    public String removeBookFromWishlist(@PathVariable("bookId") long bookId, @Valid User user, Model model) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + bookId));
        String mess;
        User currUser = userService.getCurrentLoggedUser();
        //add the book to his wishlist 
        List<Book> newWishList = bookService.removeBookFromWishList(book);
        if (newWishList.size() != currUser.getWishlist().size()) {
            mess = "Book removed successfully from wishlist";
        } else {
            mess = "Book wasn't in wishlist";
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
        model.addAttribute("user", user);
        model.addAttribute("mess", mess);

        return "redirect:/user/wishlist";
    }


    @PostMapping("/books/createRequestToBorrow/{bookId}")
    public String createRequestToBorrowBook(@PathVariable("bookId") long bookId, @Valid User user, Model model) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + bookId));
        String mess;
        User currUser = userService.getCurrentLoggedUser();

        //add the book to his wishlist
        List<Book> requestToB = bookService.createABorrowRequest(book);
        if (requestToB.size() != currUser.getBorrowRequests().size()) {
            mess = "Book already is requested";
        } else {
            mess = "Book requested successfully";
        }

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
        model.addAttribute("user", user);
        model.addAttribute("mess", mess);

        return "redirect:/profile";
    }

    //show to the staff what are the current pending requests, so he can create a report and lend the book to the user
    @GetMapping("/pending-borrow-requests")
    public String showPendingRequests(Model model) {
        List<User> users;
        List<Book> allBorrowRequests = new ArrayList<>();
        List<User> usersToList = new ArrayList<>();
        users = userRepository.findAll();

        for (User user : users) {
            List<Book> userBorrowRequests = user.getBorrowRequests();
            if (!userBorrowRequests.isEmpty()) {
                usersToList.add(user);
            }
        }
        for (User user : users) {
            List<Book> userBorrowRequests = user.getBorrowRequests();
            allBorrowRequests.addAll(userBorrowRequests);
        }
        model.addAttribute("borrowRequests", allBorrowRequests);
        model.addAttribute("users", usersToList);

        return "pending-borrow-requests";
    }
}