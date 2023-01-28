package com.example.securityTest;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

/**
 *
 * @author Lenovo
 */
@Entity

@Table(name = "authors")
public class Author implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "author_id")
    private long authorId;

    @NotBlank(message = "Author name is mandatory")
    @Column(name = "author_name")
    private String authorName;

    @NotBlank(message = "Nationality is mandatory")
    @Column(name = "nationality")
    private String nationality;

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    @OneToMany(targetEntity = Book.class, mappedBy = "author", cascade = CascadeType.ALL)
    private Set<Book> books;

    public Set<Book> getBooks() {
        return books;
    }

    public Author() {

    }

    public Author(long authorId, String authorName, String nationality) {
        this.authorId = authorId;
        this.authorName = authorName;
        this.nationality = nationality;
    }

}
