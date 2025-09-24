package com.likelion.sbstudy.domain.book.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.likelion.sbstudy.domain.book.dto.request.CreateBookRequest;
import com.likelion.sbstudy.domain.book.dto.response.BookResponse;
import com.likelion.sbstudy.domain.book.entity.Category;
import com.likelion.sbstudy.domain.book.service.BookServiceImpl;
import com.likelion.sbstudy.global.response.BaseResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BookControllerImpl implements BookController {

  private final BookServiceImpl bookService;

  @Override
  public ResponseEntity<BaseResponse<BookResponse>> createBook(
      @RequestPart(value = "imageList", required = false) List<MultipartFile> imageList,
      @Valid @RequestPart(value = "book") CreateBookRequest request,
      @RequestPart(value = "categoryList") List<Category> categoryList) {

    BookResponse response = bookService.createBook(imageList, request, categoryList);

    return ResponseEntity.ok(BaseResponse.success(201, "책 생성에 성공하였습니다.", response));
  }

  @Override
  public ResponseEntity<BaseResponse<List<BookResponse>>> getAllBooks() {

    List<BookResponse> response = bookService.getAllBooks();
    return ResponseEntity.ok(BaseResponse.success(200, "전체 책 조회에 성공했습니다.", response));
  }

  @Override
  public ResponseEntity<BaseResponse<BookResponse>> getBook(Long id) {

    BookResponse response = bookService.getBook(id);
    return ResponseEntity.ok(BaseResponse.success(200, "책 단일 조회에 성공했습니다.", response));
  }

  @Override
  public ResponseEntity<BaseResponse<BookResponse>> updateBook(
      Long id,
      @RequestPart(value = "imageList", required = false) List<MultipartFile> imageList,
      @Valid @RequestPart(value = "book") CreateBookRequest request,
      @RequestPart(value = "categoryList") List<Category> categoryList) {

    BookResponse response = bookService.updateBook(id, imageList, request, categoryList);
    return ResponseEntity.ok(BaseResponse.success(200, "책 수정에 성공했습니다.", response));
  }

  @Override
  public ResponseEntity<BaseResponse<Void>> deleteBook(Long id) {

    bookService.deleteBook(id);
    return ResponseEntity.ok(BaseResponse.success(204, "책 삭제에 성공했습니다.", null));
  }
}
