package com.app.tuantuan.model.base;

import com.app.tuantuan.constant.CustomException;
import com.app.tuantuan.constant.HttpResponseStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Resp<T> {
  protected int errorCode;
  protected String errorMessage;
  private T data;
  private boolean success = false;

  private Resp() {}

  public static Resp<Void> ok() {
    return ok(HttpResponseStatus.OK.code(), true, HttpResponseStatus.OK.reasonPhrase());
  }

  public static Resp<Void> ok(int errorCode, boolean success, String message) {

    Resp<Void> resp= error(errorCode,message);
    resp.setSuccess(success);
    return resp;
  }

  public static <T> Resp<T> data(T data) {
    Resp<T> resp = new Resp<>();
    resp.setErrorCode(HttpResponseStatus.OK.code());
    resp.setErrorMessage(HttpResponseStatus.OK.reasonPhrase());
    resp.setData(data);
    resp.setSuccess(true);
    return resp;
  }

  public static Resp<Void> error(CustomException ex) {
    Resp<Void> resp = new Resp<>();
    resp.setErrorCode(ex.getCode());
    resp.setErrorMessage(ex.getMessage());
    return resp;
  }

  public static Resp<Void> error(int errorCode, String errorMessage) {
    Resp<Void> resp = new Resp<>();
    resp.setErrorCode(errorCode);
    resp.setErrorMessage(errorMessage);
    return resp;
  }

  public static Resp<Void> internalServerError(String message) {
    Resp<Void> resp = new Resp<>();
    resp.setErrorCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
    resp.setErrorMessage(message);
    return resp;
  }
}
