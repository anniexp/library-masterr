/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.securityTest;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

/**
 *
 * @author Lenovo
 */
@Service
public class AuthorService {

    @Autowired
    AuthorRepository authorRepository;

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
}
