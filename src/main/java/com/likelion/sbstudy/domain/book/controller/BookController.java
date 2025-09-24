package com.likelion.sbstudy.domain.book.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.likelion.sbstudy.domain.book.dto.request.CreateBookRequest;
import com.likelion.sbstudy.domain.book.dto.response.BookResponse;
import com.likelion.sbstudy.domain.book.entity.Category;
import com.likelion.sbstudy.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Book", description = "책 관련 API")
@RequestMapping("/api/books")
public interface BookController {

  @Operation(summary = "새 책 등록", description = "새로운 책을 등록하고, 등록된 책 정보를 반환합니다.")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<BaseResponse<BookResponse>> createBook(
      @Parameter(
              description = "책 이미지 파일",
              content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
          @RequestPart(value = "imageList", required = false)
          List<MultipartFile> imageList,
      @Parameter(
              description = "책 등록 요청 정보",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
          @Valid
          @RequestPart(value = "book")
          CreateBookRequest request,
      @Parameter(
              description = "책 카테고리 리스트",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
          @RequestParam(value = "categoryList")
          List<Category> categoryList);

  @Operation(summary = "책 전체 조회", description = "모든 책 정보를 조회합니다.")
  @GetMapping
  ResponseEntity<BaseResponse<List<BookResponse>>> getAllBooks();

  @Operation(summary = "책 단일 조회", description = "식별자로 책 정보를 조회합니다.")
  @GetMapping("/{id}")
  ResponseEntity<BaseResponse<BookResponse>> getBook(
      @Parameter(description = "조회할 책 ID", example = "1") @PathVariable Long id);

  @Operation(summary = "책 수정", description = "책 정보를 수정하고 수정된 정보를 반환합니다.")
  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<BaseResponse<BookResponse>> updateBook(
      @Parameter(description = "수정할 책 식별자", example = "1") @PathVariable Long id,
      @Parameter(
              description = "수정할 이미지 리스트",
              content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
          @RequestPart(value = "imageList", required = false)
          List<MultipartFile> imageList,
      @Parameter(
              description = "책 수정 요청 정보",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
          @Valid
          @RequestPart(value = "book")
          CreateBookRequest request,
      @Parameter(
              description = "수정할 카테고리 리스트",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
          @RequestParam(value = "categoryList")
          List<Category> categoryList);

  @Operation(summary = "책 삭제", description = "식별자로 책을 삭제합니다.")
  @DeleteMapping("/{id}")
  ResponseEntity<BaseResponse<Void>> deleteBook(
      @Parameter(description = "삭제할 책 ID") @PathVariable Long id);
}
