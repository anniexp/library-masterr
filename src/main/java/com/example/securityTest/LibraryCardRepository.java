package com.example.securityTest;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author user
 */
@Repository
public interface LibraryCardRepository extends JpaRepository<LibraryCard, Long>{
    List<LibraryCard> findByCardNumber(String cardNumber);
}
