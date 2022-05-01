package com.example.securityTest;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

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

 
      /*  @OneToOne(fetch = FetchType.LAZY, optional = true, mappedBy = "book")
    private Report report;*/
     @OneToMany(targetEntity = Report.class, mappedBy = "book",  cascade = CascadeType.ALL)
    private Set<Report> report;
      
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
    @Size(min = 2, max = 255, message="must be between 2 and 255 characters long")
    private String series;
 
 @Column(name = "genres", nullable= true)
    @Size(min = 0, max = 255, message="must be between 2 and 255 characters long")
    private String genres;
 
 @Column(name = "description", nullable= true)
    @Size(min = 2, max = 255, message="must be between 2 and 1000 characters long")
    private String description;
 
 @Column(name = "date_added", updatable = false)
    @CreationTimestamp
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dateAdded;
 
 
 
 
 
 
 
 
 /*
 @Column(name = "ratings",  nullable= true)
  private List<Integer> ratings;
 

    public List<Integer> getRatings() {
        return ratings;
    }

    public void setRatings(List<Integer> ratings) {
        this.ratings = ratings;
    }*/
@Column(name = "book_picture", nullable= true)
 private String bookPicture;

 private long pictureSize;
 
 private byte [] pictureContent;
 
 
 
 
 
 
   @ManyToMany(mappedBy = "wishlist", fetch = FetchType.LAZY)
    private List<User> usersOfWishlist;
   
    @ManyToMany(mappedBy = "borrowRequests", fetch = FetchType.LAZY)
    private List<User> borrowRequests;
    

    public List<User> getUsersOfWishlist() {
        return usersOfWishlist;
    }

    public void setUsersOfWishlist(List<User> usersOfWishlist) {
        this.usersOfWishlist = usersOfWishlist;
    }

    public List<User> getBorrowRequests() {
        return borrowRequests;
    }

    public void setBorrowRequests(List<User> borrowRequests) {
        this.borrowRequests = borrowRequests;
    }
 
 
 
 
 
 
 
 
 

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getBookPicture() {
        return bookPicture;
    }

    public void setBookPicture(String bookPicture) {
        this.bookPicture = bookPicture;
    }

    public long getPictureSize() {
        return pictureSize;
    }

    public void setPictureSize(long pictureSize) {
        this.pictureSize = pictureSize;
    }

    public byte[] getPictureContent() {
        return pictureContent;
    }

    public void setPictureContent(byte[] pictureContent) {
        this.pictureContent = pictureContent;
    }
    


public Book() {

    }

    public Book(long bookId, int yearOfPublishing, boolean isRented, Author author, String title, String isbn, Set<Report> report, String publisher, int pages, int volume, String edition, String series, String genres, String description, Date dateAdded, String bookPicture, long pictureSize, byte[] pictureContent, List<User> usersOfWishlist, List<User> borrowRequests) {
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
        this.genres = genres;
        this.description = description;
        this.dateAdded = dateAdded;
        this.bookPicture = bookPicture;
        this.pictureSize = pictureSize;
        this.pictureContent = pictureContent;
        this.usersOfWishlist = usersOfWishlist;
        this.borrowRequests = borrowRequests;
    }

  

    

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
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

    public Set<Report> getReport() {
        return report;
    }

    public void setReport(Set<Report> report) {
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
