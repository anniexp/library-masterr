/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.securityTest;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author Lenovo
 */
@Entity
@Table(name="report")
public class Report 
    implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "report_id")
    private long reportId;
    
   // @OnDelete(action = OnDeleteAction.CASCADE)
   @OneToOne(targetEntity = Book.class, fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = false)
   @JoinColumn(name = "book_id" , referencedColumnName = "id")
    private Book book;

    

@ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User borrower;

    public User getBorrower() {
        return borrower;
    }

    public void setBorrower(User borrower) {
        this.borrower = borrower;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Report(long reportId, Book book, User borrower, Date dateCreated, Date lastUpdated) {
        this.reportId = reportId;
        this.book = book;
        this.borrower = borrower;
        this.dateCreated = dateCreated;
        this.lastUpdated = lastUpdated;
    }

    
    
   // @NotBlank(message = "Author name is mandatory")
    //private String author;
@Column(name = "date_created", updatable = false)
    @CreationTimestamp
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dateCreated;

    @Column(name = "last_updated", updatable = false) 
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date lastUpdated;
    
     @Column(name = "is_returned", updatable = true)
    private boolean isReturned;
     
     
     

    public boolean isIsReturned() {
        return isReturned;
    }

    public void setIsReturned(boolean isReturned) {
        this.isReturned = isReturned;
    }
   

    public Report() {
    }

   
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    


   
    public long getReportId() {
        return reportId;
    }

    public void setReportId(long reportId) {
        this.reportId = reportId;
    }

   
      
}
