package com.nguyenhan.maddemo1.exception;

public class AccountNotVerifiedException extends RuntimeException{
    public AccountNotVerifiedException(String message) {
        super(message);
    }
}
