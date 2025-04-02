package com.nguyenhan.maddemo1.exception;

public class ResourceAlreadyExistsException extends RuntimeException{
    public ResourceAlreadyExistsException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("Resource %s already exists for field %s with value %s", resourceName, fieldName, fieldValue));
    }
}
