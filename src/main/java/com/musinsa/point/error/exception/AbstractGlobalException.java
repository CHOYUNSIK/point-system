package com.musinsa.point.error.exception;




import com.musinsa.point.error.code.ResponseEnumType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public abstract class AbstractGlobalException extends RuntimeException{

    protected ResponseEnumType code;
    protected String message;

    public AbstractGlobalException(ResponseEnumType responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode;
        this.message = responseCode.getMessage();
    }

    public AbstractGlobalException(String message , ResponseEnumType responseCode) {
        super(String.join(" : ", responseCode.getMessage(), message));
        this.message =  String.join(" : ", responseCode.getMessage(), message);
        this.code = responseCode;
    }

    public AbstractGlobalException(String message, ResponseEnumType responseCode, Throwable cause) {
        super(String.join(" : ", responseCode.getMessage(), message), cause);
        this.message = String.join(" : ", responseCode.getMessage(), message);
        this.code = responseCode;
    }

    public AbstractGlobalException(ResponseEnumType responseCode, Throwable cause) {
        super(responseCode.getMessage(), cause);
        this.code = responseCode;
        this.message = responseCode.getMessage();
    }
}
