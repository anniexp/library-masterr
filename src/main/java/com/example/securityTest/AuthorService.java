package com.example.securityTest;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author Lenovo
 */
@Service
public class AuthorService {
   private final AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    List<Author> findByAuthorName(String authorName) {
        return authorRepository.findByAuthorName(authorName);
    }

    List<Author> findByAuthorNameStartingWith(String authorName) {
        return authorRepository.findByAuthorNameStartingWith(authorName);
    }

    public Optional<Author> findById(Long id) {
        return authorRepository.findById(id);
    }

    public void delete(Author author) {
        authorRepository.delete(author);
    }

    public void save(Author author) {
        authorRepository.save(author);
    }

    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    public Page<Author> findAll(Pageable paging) {
        return authorRepository.findAll(paging);
    }

    public Page<Author> findByKeyword(String authorName, Pageable paging) {
        return authorRepository.findByKeyword(authorName, paging);
    }
}
