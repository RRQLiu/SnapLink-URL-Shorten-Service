
// 4. Exception class
package com.snaplink.exception;

public class UrlNotFoundException extends RuntimeException {
    public UrlNotFoundException(String message) {
        super(message);
    }
}