package com.jxufe.Exception;

public class UserException extends RuntimeException{

    public UserException(){

    }

    public UserException(String msg){
        super(msg);
    }
}
