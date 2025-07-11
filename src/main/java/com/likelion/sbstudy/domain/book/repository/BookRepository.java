package com.likelion.sbstudy.domain.book.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion.sbstudy.domain.book.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

  Optional<Book> findByTitleAndAuthor(String title, String author);
}
