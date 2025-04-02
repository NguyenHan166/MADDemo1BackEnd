package com.nguyenhan.maddemo1.constants;

import org.springframework.http.HttpStatus;

public class ResponseConstants {

    public ResponseConstants() {}

    public static final int STATUS_200 = HttpStatus.OK.value();
    public static final int STATUS_201 = HttpStatus.CREATED.value();
    public static final int STATUS_400 = HttpStatus.BAD_REQUEST.value();
    public static final int STATUS_401 = HttpStatus.UNAUTHORIZED.value();
    public static final int STATUS_403 = HttpStatus.FORBIDDEN.value();
    public static final int STATUS_404 = HttpStatus.NOT_FOUND.value();
    public static final int STATUS_409 = HttpStatus.CONFLICT.value();
    public static final int STATUS_417 = HttpStatus.EXPECTATION_FAILED.value();
    public static final int STATUS_500 = HttpStatus.INTERNAL_SERVER_ERROR.value();

    public static final String MESSAGE_200 = "Request processed successfully";
    public static final String MESSAGE_201 = "Resource created successfully";
    public static final String MESSAGE_400 = "Bad Request: Invalid input parameters";
    public static final String MESSAGE_401 = "Unauthorized: Authentication is required";
    public static final String MESSAGE_403 = "Forbidden: You do not have permission to access this resource";
    public static final String MESSAGE_404 = "Resource not found";
    public static final String MESSAGE_409 = "Conflict: Resource already exists";
    public static final String MESSAGE_417_UPDATE = "Update operation failed. Please try again or contact the Dev team";
    public static final String MESSAGE_417_DELETE = "Delete operation failed. Please try again or contact the Dev team";
    public static final String MESSAGE_500 = "Internal Server Error: An error occurred. Please try again or contact the Dev team";
}
