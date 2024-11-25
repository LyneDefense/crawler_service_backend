package com.app.tuantuan.config;

import com.app.tuantuan.constant.CustomException;
import com.app.tuantuan.model.base.Resp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** 统一处理异常 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ResponseStatus(HttpStatus.OK)
  @ExceptionHandler(CustomException.class)
  public Resp<Void> error(CustomException ex) {
    return Resp.error(ex.getCode(), ex.getMessage());
  }
}
