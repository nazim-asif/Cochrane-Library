package com.cochranelibrary.domain.exception;

/**
 * @author Nazim Uddin Asif
 * @Since 1.0.0
 */
public class ReviewNotFoundException extends RuntimeException{
    public ReviewNotFoundException(String exception){
        super(exception);
    }
}
