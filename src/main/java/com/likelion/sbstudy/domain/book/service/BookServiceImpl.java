package com.likelion.sbstudy.domain.book.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.likelion.sbstudy.domain.book.dto.request.CreateBookRequest;
import com.likelion.sbstudy.domain.book.dto.response.BookResponse;
import com.likelion.sbstudy.domain.book.entity.Book;
import com.likelion.sbstudy.domain.book.entity.BookImage;
import com.likelion.sbstudy.domain.book.entity.Category;
import com.likelion.sbstudy.domain.book.exception.BookErrorCode;
import com.likelion.sbstudy.domain.book.mapping.BookMapper;
import com.likelion.sbstudy.domain.book.repository.BookRepository;
import com.likelion.sbstudy.global.exception.CustomException;
import com.likelion.sbstudy.global.page.mapper.InfiniteMapper;
import com.likelion.sbstudy.global.page.mapper.PageMapper;
import com.likelion.sbstudy.global.page.response.InfiniteResponse;
import com.likelion.sbstudy.global.page.response.PageResponse;
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
  private final PageMapper pageMapper;
  private final InfiniteMapper infiniteMapper;

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
  @Transactional(readOnly = true)
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
  @Transactional(readOnly = true)
  public PageResponse<BookResponse> getBookPageByCategory(Category category, Pageable pageable) {

    Page<BookResponse> bookPage =
        bookRepository
            .findAllByCategoryListContaining(category, pageable)
            .map(bookMapper::toBookResponse);

    log.info(
        "책 페이지 조회 성공: category={}, pageNumber={}, totalElements={}",
        category,
        pageable.getPageNumber(),
        bookPage.getTotalElements());
    return pageMapper.toBookPageResponse(bookPage);
  }

  @Override
  @Transactional(readOnly = true)
  public InfiniteResponse<BookResponse> getBooksByCategoryInfinite(
      Category category, Long lastBookId, Integer size) {

    Pageable pageable = PageRequest.of(0, size + 1, Sort.by(Sort.Direction.DESC, "id"));
    List<Book> books;

    if (lastBookId == null) {
      books = bookRepository.findAllByCategoryListContaining(category, pageable).getContent();
    } else {
      books =
          bookRepository
              .findAllByCategoryListContainingAndIdLessThan(category, lastBookId, pageable)
              .getContent();
    }

    boolean hasNext = books.size() > size;
    if (hasNext) {
      books = books.subList(0, size);
    }

    List<BookResponse> bookResponseList = books.stream().map(bookMapper::toBookResponse).toList();

    Long newLastCursor = books.isEmpty() ? null : books.getLast().getId();

    log.info("책 인피니티 스크롤 조회 성공: category={}, lastBookId={}, size={}", category, lastBookId, size);
    return infiniteMapper.toBookInfiniteResponse(bookResponseList, newLastCursor, hasNext, size);
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
