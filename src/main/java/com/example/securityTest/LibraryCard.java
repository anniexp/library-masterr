/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.securityTest;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;

/**
 *
 * @author user
 */
@Entity
@Table(name = "library_cards")

public class LibraryCard implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "card_id")
    private long cardId;
  
    @Column(name = "card_number",unique = true, nullable = false, length = 255)
    private String cardNumber;
    
    @Column(name = "first_name", nullable = false, length = 200)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 200)
    private String lastName;

    /*@Column(name = "email", unique = true, nullable = false, length = 100)
    @Email
    @NotBlank
    private String email;*/
    
    @Column(name = "date_created", updatable = false)
    @CreationTimestamp
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dateCreated;

    @Column(name = "expiration_date", updatable = false) 
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date cardExpirationDate;
    
    @Column(name = "is_expired", nullable = false)
	private boolean isExpired;

   

    public long getCardId() {
        return cardId;
    }

    public boolean isIsExpired() {
        return isExpired;
    }

    public void setIsExpired(boolean isExpired) {
        this.isExpired = isExpired;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
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

    public LibraryCard(long cardId, String cardNumber, String firstName, String lastName, Date dateCreated, Date cardExpirationDate, boolean isExpired) {
        this.cardId = cardId;
        this.cardNumber = cardNumber;
        this.firstName = firstName;
        this.lastName = lastName;
     
        this.dateCreated = dateCreated;
        this.cardExpirationDate = cardExpirationDate;
        this.isExpired = isExpired;
    }

   
   

    public LibraryCard() {
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getCardExpirationDate() {
        return cardExpirationDate;
    }

    public void setCardExpirationDate(Date cardExpirationDate) {
        this.cardExpirationDate = cardExpirationDate;
    }


}
