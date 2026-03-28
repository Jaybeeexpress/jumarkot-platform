package com.jumarkot.shared.auth;

/** Thrown when an API key cannot be validated by the configured resolver. */
public class InvalidApiKeyException extends RuntimeException {

    public InvalidApiKeyException(String message) {
        super(message);
    }
}
