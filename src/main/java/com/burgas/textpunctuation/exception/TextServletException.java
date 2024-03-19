package com.burgas.textpunctuation.exception;

public class TextServletException extends RuntimeException{

    public TextServletException() {
        super();
    }

    public TextServletException(String message) {
        super(message);
    }

    public TextServletException(String message, Throwable cause) {
        super(message, cause);
    }

    public TextServletException(Throwable cause) {
        super(cause);
    }
}
