package com.likelion.sbstudy.domain.book.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.likelion.sbstudy.domain.book.dto.request.CreateBookRequest;
import com.likelion.sbstudy.domain.book.dto.response.BookResponse;
import com.likelion.sbstudy.domain.book.entity.Book;
import com.likelion.sbstudy.domain.book.entity.BookImage;
import com.likelion.sbstudy.domain.book.entity.Category;
import com.likelion.sbstudy.domain.book.exception.BookErrorCode;
import com.likelion.sbstudy.domain.book.mapper.BookMapper;
import com.likelion.sbstudy.domain.book.repository.BookRepository;
import com.likelion.sbstudy.global.exception.CustomException;
import com.likelion.sbstudy.global.s3.entity.PathName;
import com.likelion.sbstudy.global.s3.service.S3Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

  private final BookRepository bookRepository;
  private final S3Service s3Service;
  private final BookMapper bookMapper;

  @Override
  @Transactional
  public BookResponse createBook(
      List<MultipartFile> imageList, CreateBookRequest request, List<Category> categoryList) {

    if (bookRepository.findByTitleAndAuthor(request.getTitle(), request.getAuthor()).isPresent()) {
      throw new CustomException(BookErrorCode.BOOK_ALREADY_EXISTS);
    }

    Book book = bookMapper.toBook(request);

    List<BookImage> bookImageList =
        Optional.ofNullable(imageList).orElse(List.of()).stream()
            .filter(image -> !image.isEmpty())
            .map(
                image -> {
                  String imageUrl = s3Service.uploadFile(PathName.FOLDER1, image);
                  return BookImage.builder().imageUrl(imageUrl).book(book).build();
                })
            .toList();

    book.addBookImageList(bookImageList);
    book.addCategoryList(categoryList);

    bookRepository.save(book);
    log.info("책 생성 성공: title={}", book.getTitle());

    return bookMapper.toBookResponse(book);
  }

  @Override
  @Transactional
  public BookResponse getBook(Long id) {

    Book book =
        bookRepository
            .findById(id)
            .orElseThrow(() -> new CustomException(BookErrorCode.BOOK_NOT_FOUND));

    log.info("책 조회 성공: id={}, title={}", book.getId(), book.getTitle());
    return bookMapper.toBookResponse(book);
  }

  @Override
  @Transactional(readOnly = true)
  public List<BookResponse> getAllBooks() {

    log.info("책 전체 조회 성공: count={}", bookRepository.count());
    return bookRepository.findAll().stream().map(bookMapper::toBookResponse).toList();
  }

  @Override
  @Transactional
  public BookResponse updateBook(
      Long id,
      List<MultipartFile> imageList,
      CreateBookRequest request,
      List<Category> categoryList) {

    Book book =
        bookRepository
            .findById(id)
            .orElseThrow(() -> new CustomException(BookErrorCode.BOOK_NOT_FOUND));

    List<BookImage> bookImageList =
        Optional.ofNullable(imageList).orElse(List.of()).stream()
            .filter(image -> !image.isEmpty())
            .map(
                image -> {
                  String imageUrl = s3Service.uploadFile(PathName.FOLDER1, image);
                  return BookImage.builder().imageUrl(imageUrl).book(book).build();
                })
            .toList();

    book.update(book);

    book.getBookImageList().clear();
    book.getBookImageList().addAll(bookImageList);
    book.getCategoryList().clear();
    book.getCategoryList().addAll(categoryList);

    log.info("책 수정 성공: id={}, title={}", book.getId(), book.getTitle());
    return bookMapper.toBookResponse(book);
  }

  @Override
  @Transactional
  public void deleteBook(Long id) {

    Book book =
        bookRepository
            .findById(id)
            .orElseThrow(() -> new CustomException(BookErrorCode.BOOK_NOT_FOUND));

    bookRepository.delete(book);
    log.info("책 삭제 성공: id={}, title={}", book.getId(), book.getTitle());
  }
}
