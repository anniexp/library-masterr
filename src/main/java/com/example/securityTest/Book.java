package com.example.securityTest;

import java.io.Serializable;
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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "book")
public class Book implements Serializable {

    //@Null(groups = OnCreate.class)
   // @NotNull(groups = OnUpdate.class)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long bookId;

    // @NotBlank(message = "Author name is mandatory")
    //private String author;
    @Column(name = "year_of_publishing")
    @Max(2030)
    private int yearOfPublishing;
    @Column(name = "is_rented", updatable = true)
    private boolean isRented;

    @ManyToOne
    @JoinColumn(name="author_id", nullable=false)
    private Author author;
    //private Integer authorId;
    //@NotBlank(message = "Author id is mandatory") @Max(255)@Min(1)
    // private long authorId;

    @NotBlank(message = "Title is mandatory")
    @Column(name = "title")
    @Size(min = 2, max = 255)
    private String title;

    @NotBlank(message = "isbn is mandatory")
    @Column(unique = true, name="isbn")
    private String isbn;

    public Book(long bookId, Author author, long authorId, String title, String isbn, int yearOfPublishing, boolean isRented, Report report) {
        this.bookId = bookId;
        this.title = title;
        this.isbn = isbn;
        this.yearOfPublishing = yearOfPublishing;
        this.isRented = isRented;
        this.author = author;
        this.report = report;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

   
    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getYearOfPublishing() {
        return yearOfPublishing;
    }

    public void setYearOfPublishing(int yearOfPublishing) {
        this.yearOfPublishing = yearOfPublishing;
    }

    public boolean isIsRented() {
        return isRented;
    }

    public void setIsRented(boolean isRented) {
        this.isRented = isRented;
    }

    @Override
    public String toString() {
        return "Book{" + "bookId=" + bookId + ", yearOfPublishing=" + yearOfPublishing + ", isRented=" + isRented + ", author=" + author + ", title=" + title + ", isbn=" + isbn + '}';
    }

    public Book() {

    }

    
   /* @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "report_id")*/
    
    
        @OneToOne(fetch = FetchType.LAZY, optional = true, mappedBy = "book")
    private Report report;

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}

    
   
    
   
    // standard constructors / setters / getters / toString
