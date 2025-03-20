package com.nguyenhan.maddemo1.constants;

import org.springframework.http.HttpStatus;

public class UsersConstants {

    private UsersConstants() {
        // restrict instantiation
    }


    public static final HttpStatus STATUS_200 = HttpStatus.OK;
    public static final HttpStatus STATUS_201 = HttpStatus.CREATED;
    public static final HttpStatus STATUS_400 = HttpStatus.BAD_REQUEST;
    public static final HttpStatus STATUS_401 = HttpStatus.UNAUTHORIZED;
    public static final HttpStatus STATUS_403 = HttpStatus.FORBIDDEN;
    public static final HttpStatus STATUS_404 = HttpStatus.NOT_FOUND;
    public static final HttpStatus STATUS_409 = HttpStatus.CONFLICT;
    public static final HttpStatus STATUS_417 = HttpStatus.EXPECTATION_FAILED;
    public static final HttpStatus STATUS_500 = HttpStatus.INTERNAL_SERVER_ERROR;

    public static final String MESSAGE_200 = "Request processed successfully";
    public static final String MESSAGE_201 = "User created successfully";
    public static final String MESSAGE_400 = "Bad Request: Invalid input parameters";
    public static final String MESSAGE_401 = "Unauthorized: Authentication is required";
    public static final String MESSAGE_403 = "Forbidden: You do not have permission to access this resource";
    public static final String MESSAGE_404 = "User not found";
    public static final String MESSAGE_409 = "Conflict: User already exists";
    public static final String MESSAGE_417_UPDATE = "Update operation failed. Please try again or contact the Dev team";
    public static final String MESSAGE_417_DELETE = "Delete operation failed. Please try again or contact the Dev team";
    public static final String MESSAGE_500 = "Internal Server Error: An error occurred. Please try again or contact the Dev team";
}

