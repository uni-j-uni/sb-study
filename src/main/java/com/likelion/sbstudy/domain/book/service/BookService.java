package com.likelion.sbstudy.domain.book.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.likelion.sbstudy.domain.book.dto.request.CreateBookRequest;
import com.likelion.sbstudy.domain.book.dto.response.BookResponse;
import com.likelion.sbstudy.domain.book.entity.Category;
import com.likelion.sbstudy.global.page.response.InfiniteResponse;
import com.likelion.sbstudy.global.page.response.PageResponse;

/**
 * 도서 관련 주요 기능을 제공하는 서비스 인터페이스입니다.
 *
 * <p>주요 기능:
 *
 * <ul>
 *   <li>도서 생성
 *   <li>도서 상세 조회
 *   <li>전체 도서 조회
 *   <li>카테고리별 도서 페이지 조회
 *   <li>도서 수정
 *   <li>도서 삭제
 * </ul>
 */
public interface BookService {

  /**
   * 새로운 도서를 생성합니다.
   *
   * @param imageList 도서 이미지 파일 목록
   * @param request 도서 생성 요청 데이터
   * @param categoryList 도서에 적용할 카테고리 목록
   * @return 생성된 도서의 상세 정보
   */
  BookResponse createBook(
      List<MultipartFile> imageList, CreateBookRequest request, List<Category> categoryList);

  /**
   * 특정 도서 ID로 도서 상세 정보를 조회합니다.
   *
   * @param id 조회할 도서 ID
   * @return 해당 도서의 상세 정보
   */
  BookResponse getBook(Long id);

  /**
   * 모든 도서 목록을 조회합니다.
   *
   * @return 전체 도서 목록
   */
  List<BookResponse> getAllBooks();

  /**
   * 특정 카테고리에 속한 도서를 페이지 단위로 조회합니다.
   *
   * @param category 조회할 도서 카테고리
   * @param pageable 페이징 및 정렬 정보
   * @return 카테고리별 도서 목록 페이지
   */
  PageResponse<BookResponse> getBookPageByCategory(Category category, Pageable pageable);

  /**
   * 특정 카테고리에 속한 도서를 인피니티 스크롤 방식으로 조회합니다.
   *
   * @param category 조회할 도서 카테고리
   * @param lastBookId 이전 조회에서 마지막으로 가져온 도서 ID (처음 조회 시 null)
   * @param size 한 번에 가져올 도서 개수
   * @return 조회된 도서 목록
   */
  InfiniteResponse<BookResponse> getBooksByCategoryInfinite(
      Category category, Long lastBookId, Integer size);

  /**
   * 특정 도서를 수정합니다.
   *
   * @param id 수정할 도서 ID
   * @param imageList 수정할 이미지 파일 목록
   * @param request 도서 수정 요청 데이터
   * @param categoryList 수정할 카테고리 목록
   * @return 수정된 도서의 상세 정보
   */
  BookResponse updateBook(
      Long id,
      List<MultipartFile> imageList,
      CreateBookRequest request,
      List<Category> categoryList);

  /**
   * 특정 도서를 삭제합니다.
   *
   * @param id 삭제할 도서 ID
   */
  void deleteBook(Long id);
}
