package com.connectx.exception;

public class GroupMemberNotFoundException extends RuntimeException {

    public GroupMemberNotFoundException(String message) {
        super(message);
    }
}
