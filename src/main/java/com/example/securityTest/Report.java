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

    
@Column(name = "Borrower")
    
private String borrower;

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getBorrower() {
        return borrower;
    }

    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }
        
   // @NotBlank(message = "Author name is mandatory")
    //private String author;
@Column(name = "date_created")
    @CreationTimestamp
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dateCreated;

    @Column(name = "last_updated")
    @UpdateTimestamp
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date lastUpdated;

    public Report() {
    }

    public Report(long reportId, String borrower, Book book, Date dateCreated, Date lastUpdated) {
        this.reportId = reportId;
        this.borrower = borrower;
        this.book = book;
        this.dateCreated = dateCreated;
        this.lastUpdated = lastUpdated;
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
