package com.jwtWithoauth.jwtWithoauth.global.error.exception;

import com.jwtWithoauth.jwtWithoauth.global.error.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{

    private ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode){
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
