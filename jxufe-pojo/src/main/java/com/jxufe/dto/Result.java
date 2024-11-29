package com.jxufe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    private Boolean success;

    private String errorMsg;

    public Result(Boolean success) {
        this.success = success;
    }

    private T data;

    public static <T> Result<T> ok(){
        return new Result<T>(true);
    }

    public static <T> Result<T> ok(T data){
        return new Result<T>(true, null, data);
    }

    public static <T> Result<T> fail(String errorMsg){
        return new Result<T>(false, errorMsg, null);
    }
}
