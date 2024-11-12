package com.ta.llmbackend.util;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.util.List;

public class ResponseUtil {

    public static ResponseEntity<Object> okResponse(Object data, String message) {
        BaseResponse result = new BaseResponse();
        result.setCode(HttpStatus.OK.value());
        result.setStatus(HttpStatus.OK.name());
        result.setContent(data);
        result.setMessage(message);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    public static ResponseEntity<Object> createdResponse(Object data, String message) {
        BaseResponse result = new BaseResponse();
        result.setCode(HttpStatus.CREATED.value());
        result.setStatus(HttpStatus.OK.name());
        result.setContent(data);
        result.setMessage(message);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    public static ResponseEntity<Object> okDownloadResponse(byte[] data, String filename, String message) {
        BaseResponse result = new BaseResponse();
        result.setCode(HttpStatus.OK.value());
        result.setStatus(HttpStatus.OK.name());
        result.setContent(new InputStreamResource(new ByteArrayInputStream(data)));
        result.setMessage(message);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename)
                .body(result);
    }

    public static ResponseEntity<Object> failResponse(String message) {
        BaseResponse result = new BaseResponse();
        result.setCode(HttpStatus.EXPECTATION_FAILED.value());
        result.setStatus(HttpStatus.EXPECTATION_FAILED.name());
        result.setContent(null);
        result.setMessage(message);
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(result);
    }

    public static ResponseEntity<Object> exception(String message, HttpStatus httpStatus) {
        BaseResponse result = new BaseResponse();
        result.setCode(httpStatus.value());
        result.setStatus(httpStatus.name());
        result.setContent(null);
        result.setMessage(message);
        return ResponseEntity.status(httpStatus).body(result);
    }

    public static ResponseEntity<Object> badRequest(String message, List<String> errors) {
        BaseResponse result = new BaseResponse();
        result.setCode(HttpStatus.BAD_REQUEST.value());
        result.setStatus(HttpStatus.BAD_REQUEST.name());
        result.setContent(null);
        result.setMessage(message);
        result.setErrors(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    public static ResponseEntity<Object> badRequest(String message, List<String> errors, HttpStatus httpStatus) {
        BaseResponse result = new BaseResponse();
        result.setCode(httpStatus.value());
        result.setStatus(httpStatus.name());
        result.setContent(null);
        result.setMessage(message);
        result.setErrors(errors);
        return ResponseEntity.status(httpStatus).body(result);
    }
}
