package com.nguyenhan.maddemo1.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("Resource %s not found for field %s with value %s", resourceName, fieldName, fieldValue));
    }

}
