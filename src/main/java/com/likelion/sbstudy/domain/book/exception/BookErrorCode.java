package com.likelion.sbstudy.domain.book.exception;

import org.springframework.http.HttpStatus;

import com.likelion.sbstudy.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookErrorCode implements BaseErrorCode {
  BOOK_ALREADY_EXISTS("BOOK4001", "이미 존재하는 책입니다.", HttpStatus.BAD_REQUEST);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
