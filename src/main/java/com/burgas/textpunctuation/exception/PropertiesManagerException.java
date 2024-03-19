package com.burgas.textpunctuation.exception;

public class PropertiesManagerException extends RuntimeException{

    public PropertiesManagerException() {
        super();
    }

    public PropertiesManagerException(String message) {
        super(message);
    }

    public PropertiesManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public PropertiesManagerException(Throwable cause) {
        super(cause);
    }
}
