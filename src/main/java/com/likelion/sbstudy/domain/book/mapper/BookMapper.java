package com.likelion.sbstudy.domain.book.mapper;

import org.springframework.stereotype.Component;

import com.likelion.sbstudy.domain.book.dto.response.BookResponse;
import com.likelion.sbstudy.domain.book.entity.Book;
import com.likelion.sbstudy.domain.book.entity.BookImage;

@Component
public class BookMapper {

  public BookResponse toBookResponse(Book book) {
    return BookResponse.builder()
        .id(book.getId())
        .title(book.getTitle())
        .author(book.getAuthor())
        .publisher(book.getPublisher())
        .price(book.getPrice())
        .description(book.getDescription())
        .releaseDate(book.getReleaseDate())
        .categoryList(book.getCategoryList())
        .bookImagesUrl(book.getBookImages().stream().map(BookImage::getImageUrl).toList())
        .build();
  }
}
