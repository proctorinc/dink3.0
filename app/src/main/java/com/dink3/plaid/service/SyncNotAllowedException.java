package com.dink3.plaid.service;

public class SyncNotAllowedException extends RuntimeException {
    public SyncNotAllowedException(String message) {
        super(message);
    }
} 