package com.nguyenhan.maddemo1.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ResponseDto {

    private HttpStatus statusCode;
    private String statusMsg;

}