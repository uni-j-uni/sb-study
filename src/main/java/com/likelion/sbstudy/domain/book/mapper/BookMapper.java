package com.likelion.sbstudy.domain.book.mapper;

import org.springframework.stereotype.Component;

import com.likelion.sbstudy.domain.book.dto.request.CreateBookRequest;
import com.likelion.sbstudy.domain.book.dto.response.BookResponse;
import com.likelion.sbstudy.domain.book.entity.Book;
import com.likelion.sbstudy.domain.book.entity.BookImage;

@Component
public class BookMapper {

  public Book toBook(CreateBookRequest bookRequest) {
    return Book.builder()
        .title(bookRequest.getTitle())
        .author(bookRequest.getAuthor())
        .publisher(bookRequest.getPublisher())
        .price(bookRequest.getPrice())
        .description(bookRequest.getDescription())
        .releaseDate(bookRequest.getReleaseDate())
        .build();
  }

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
        .bookImagesUrl(book.getBookImageList().stream().map(BookImage::getImageUrl).toList())
        .build();
  }
}
