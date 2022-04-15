package com.example.securityTest;

import java.io.Serializable;
import java.util.List;
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
import javax.validation.constraints.Size;

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
    @Column(unique = true, name="isbn", length = 13)
    private String isbn;

 
        @OneToOne(fetch = FetchType.LAZY, optional = true, mappedBy = "book")
    private Report report;
    
      
    @Column(name = "publisher")
    @Size(min = 2, max = 255)
    private String publisher;
         
    @Column(name = "pages")
   @Min(value=0, message="must be equal or greater than 0")  
    @Max(value=20000, message="must be equal or less than 20000") 
    private int pages;
    
    @Column(name = "volume")
  @Min(value=1, message="must be equal or greater than 1")  
    @Max(value=2000, message="must be equal or less than 2000") 
    private int volume;

     @Column(name = "edition",  nullable= true)
    @Size(min = 0, max = 255,  message="must be between 0 and 255 characters long")
    private String edition;
     
 @Column(name = "series", nullable= true)
    @Size(min = 2, max = 255, message="must be between 0 and 255 characters long")
    private String series;
 /*
 @Column(name = "ratings",  nullable= true)
  private List<Integer> ratings;
 

    public List<Integer> getRatings() {
        return ratings;
    }

    public void setRatings(List<Integer> ratings) {
        this.ratings = ratings;
    }*/

 private String profilePicture;
 private long size;
 private byte [] content;
 
    public Book(long bookId, int yearOfPublishing, boolean isRented, Author author, String title, String isbn, Report report, String publisher, int pages, int volume, String edition, String series, String profilePicture, long size, byte[] content) {
        this.bookId = bookId;
        this.yearOfPublishing = yearOfPublishing;
        this.isRented = isRented;
        this.author = author;
        this.title = title;
        this.isbn = isbn;
        this.report = report;
        this.publisher = publisher;
        this.pages = pages;
        this.volume = volume;
        this.edition = edition;
        this.series = series;
        this.profilePicture = profilePicture;
        this.size = size;
        this.content = content;
    }
    
 
 

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
    

public Book() {

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

    
    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

   

    
    
    
    
}

    
   
    
   
    // standard constructors / setters / getters / toString
