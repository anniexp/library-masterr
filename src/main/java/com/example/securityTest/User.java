/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.securityTest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Lenovo
 */

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long user_id;


	@Column(nullable = false, length = 64, name = "password")
	private String password;

	@Column(name = "username", unique = true, nullable = false, length = 20)
	private String username; 
        
        @Column(name = "email", unique = true, nullable = false, length = 100)
        @Email
        @NotBlank
	private String email;
        
         @Column(name = "phoneNumber", nullable = true, length = 20)
	private String phoneNumber;       
         
         
        @Column(name = "profilePicture", nullable= true)
        private String profilePicture;
        
        
        private long profilePictureSize;
 
        @Column(name = "pictureContent", nullable= true)
        private byte [] pictureContent;

        @Column(name = "address", nullable = true, length = 255)
	private String userAddress;
       
        @Column(name = "first_name", nullable = false, length = 200,  updatable = false)
	private String firstName; 
        
        @Column(name = "last_name", nullable = false, length = 200,  updatable = false)
	private String lastName; 
        
        @Column(name = "card_number", nullable = false, length = 255,  updatable = false)
	private String cardNumber; 
        
        
            
         
         /*
        //many to many
         @Column(name = "wishlist", nullable = true)
	private List<Book> wishlist;
        
        //many to many
         @Column(name = "borrowRequests", nullable = true)
	private List<BorrowRequests> requests;
*/
        @Column(name = "role", nullable = false)
	private String rolee;
        
        @Column(name = "enabled", nullable = false)
	private boolean enabled;
        
          @OneToMany(targetEntity = Report.class, mappedBy = "borrower", cascade = CascadeType.ALL)
    private Set<Report> borrowedBooks;


    public User() {
    }

    public User(Long user_id, String password, String username, String email, String phoneNumber, String profilePicture, long profilePictureSize, byte[] pictureContent, String userAddress, String firstName, String lastName, String cardNumber, String rolee, boolean enabled, Set<Report> borrowedBooks) {
        this.user_id = user_id;
        this.password = password;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePicture = profilePicture;
        this.profilePictureSize = profilePictureSize;
        this.pictureContent = pictureContent;
        this.userAddress = userAddress;
        this.firstName = firstName;
        this.lastName = lastName;
        this.cardNumber = cardNumber;
        this.rolee = rolee;
        this.enabled = enabled;
        this.borrowedBooks = borrowedBooks;
    }

   
    
    
    
     @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "users_wishlist",
            joinColumns = {
                    @JoinColumn(name = "userId", referencedColumnName = "user_id",
                            nullable = false, updatable = true)},
            inverseJoinColumns = {
                    @JoinColumn(name = "book_id", referencedColumnName = "id",
                            nullable = false, updatable = true)})
    private List<Book> wishlist ;

    public String getEmail() {
        return email;
    }

    public List<Book> getWishlist() {
        return wishlist;
    }

    public void setWishlist(List<Book> wishlist) {
        this.wishlist = wishlist;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public long getProfilePictureSize() {
        return profilePictureSize;
    }

    public void setProfilePictureSize(long profilePictureSize) {
        this.profilePictureSize = profilePictureSize;
    }

    public byte[] getPictureContent() {
        return pictureContent;
    }

    public void setPictureContent(byte[] pictureContent) {
        this.pictureContent = pictureContent;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

   
   
    
    
    
    public String getRolee() {
        return rolee;
    }

    public void setRolee(String rolee) {
        this.rolee = rolee;
    }

    
        
        

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
        

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    
 	

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
        
        
        
       
    public Set<Report> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void setBorrowedBooks(Set<Report> borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }

     /*@GetMapping("/users")
    public String index(Model model, @RequestParam(name = "searchAuthor", required = false) String authorName,
               @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        
        List<User> users = null;
         Pageable paging = (Pageable) PageRequest.of(page, size);  
         Page<Author> pageTuts;
        
         if (authorName != null) {
               pageTuts = authorRepository.findByKeyword(authorName, paging);
           // authors = authorRepository.findByKeyword(authorName);
        } else {
            pageTuts = userRepository.findAll(paging);
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
    }*/
    
}

