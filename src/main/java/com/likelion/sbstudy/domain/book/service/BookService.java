package com.likelion.sbstudy.domain.book.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.likelion.sbstudy.domain.book.dto.request.CreateBookRequest;
import com.likelion.sbstudy.domain.book.dto.response.BookResponse;
import com.likelion.sbstudy.domain.book.entity.Category;

public interface BookService {

  BookResponse createBook(
      List<MultipartFile> imageList, CreateBookRequest request, List<Category> categoryList);

  BookResponse getBook(Long id);

  List<BookResponse> getAllBooks();

  BookResponse updateBook(
      Long id,
      List<MultipartFile> imageList,
      CreateBookRequest request,
      List<Category> categoryList);

  void deleteBook(Long id);
}
