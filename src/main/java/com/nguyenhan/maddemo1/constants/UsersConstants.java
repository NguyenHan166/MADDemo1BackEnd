package com.nguyenhan.maddemo1.constants;

public class UsersConstants {

    private UsersConstants() {
        // restrict instantiation
    }

    public static final String STATUS_200 = "200";
    public static final String MESSAGE_200 = "Request processed successfully";

    public static final String STATUS_201 = "201";
    public static final String MESSAGE_201 = "User created successfully";

    public static final String STATUS_400 = "400";
    public static final String MESSAGE_400 = "Bad Request: Invalid input parameters";

    public static final String STATUS_401 = "401";
    public static final String MESSAGE_401 = "Unauthorized: Authentication is required";

    public static final String STATUS_403 = "403";
    public static final String MESSAGE_403 = "Forbidden: You do not have permission to access this resource";

    public static final String STATUS_404 = "404";
    public static final String MESSAGE_404 = "User not found";

    public static final String STATUS_409 = "409";
    public static final String MESSAGE_409 = "Conflict: User already exists";

    public static final String STATUS_417 = "417";
    public static final String MESSAGE_417_UPDATE = "Update operation failed. Please try again or contact the Dev team";
    public static final String MESSAGE_417_DELETE = "Delete operation failed. Please try again or contact the Dev team";

    public static final String STATUS_500 = "500";
    public static final String MESSAGE_500 = "Internal Server Error: An error occurred. Please try again or contact the Dev team";
}

