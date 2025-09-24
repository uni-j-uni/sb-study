package com.likelion.sbstudy.global.page.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.likelion.sbstudy.domain.book.dto.response.BookResponse;
import com.likelion.sbstudy.global.page.response.InfiniteResponse;

@Component
public class InfiniteMapper {

  public <T> InfiniteResponse<T> toInfiniteResponse(
      List<T> content, Long lastCursor, boolean hasNext, int size) {
    return InfiniteResponse.<T>builder()
        .content(content)
        .lastCursor(lastCursor)
        .hasNext(hasNext)
        .size(size)
        .build();
  }

  public InfiniteResponse<BookResponse> toBookInfiniteResponse(
      List<BookResponse> content, Long lastCursor, boolean hasNext, int size) {
    return toInfiniteResponse(content, lastCursor, hasNext, size);
  }
}
