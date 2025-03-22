package com.nguyenhan.maddemo1.exception;

public class VerificationCodeInvalid extends RuntimeException{
    public VerificationCodeInvalid(String message) {
        super(message);
    }
}
