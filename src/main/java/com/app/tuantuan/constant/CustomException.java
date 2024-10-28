package com.app.tuantuan.constant;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomException extends RuntimeException{

    private int code = 400;
    private String message;
    private String internalMessage;

    public CustomException(int code, String message, String internalMessage) {
        super();
        this.code = code;
        this.message = message;
        this.internalMessage = internalMessage;
    }

    public CustomException(String internalMessage) {
        super();
        this.message = internalMessage;
        this.internalMessage = internalMessage;
    }

    public CustomException(String internalMessage, Throwable e) {
        super(internalMessage, e);
        this.internalMessage = internalMessage;
    }

    public CustomException(CustomException e) {
        super();
        this.code = e.getCode();
        this.message = e.getMessage();
        this.internalMessage = e.getInternalMessage();
    }

    public CustomException(CustomException commonException, Object... args) {
        super();
        this.code = commonException.getCode();
        this.message = commonException.getMessage();
        this.internalMessage = String.format(commonException.getInternalMessage(), args);
    }
}
