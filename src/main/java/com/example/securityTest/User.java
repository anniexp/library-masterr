/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.securityTest;
import java.util.Set;
import javax.persistence.*;

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

    public User(Long user_id, String password, String username, String rolee, boolean enabled) {
        this.user_id = user_id;
        this.password = password;
        this.username = username;
        this.rolee = rolee;
        this.enabled = enabled;
    }
        
        @Column(name = "role", nullable = false)
	private String rolee;

    public User() {
    }

    public String getRolee() {
        return rolee;
    }

    public void setRolee(String rolee) {
        this.rolee = rolee;
    }

    
        
        @Column(name = "enabled", nullable = false)
	private boolean enabled;

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
        
        
        
         @OneToMany(targetEntity = Report.class, mappedBy = "borrower", cascade = CascadeType.ALL)
    private Set<Report> borrowedBooks;

    public Set<Report> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void setBorrowedBooks(Set<Report> borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }

    
}

