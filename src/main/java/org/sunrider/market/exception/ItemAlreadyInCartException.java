package org.sunrider.market.exception;

public class ItemAlreadyInCartException extends RuntimeException {

    public ItemAlreadyInCartException(String message) {
        super(message);
    }
}
