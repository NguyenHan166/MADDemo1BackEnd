package com.nguyenhan.maddemo1.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ResponseDto {

    private int statusCode;
    private String statusMsg;

}