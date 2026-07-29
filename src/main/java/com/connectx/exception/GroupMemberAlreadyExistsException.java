package com.connectx.exception;
public class GroupMemberAlreadyExistsException extends RuntimeException {

    public GroupMemberAlreadyExistsException(String message) {
        super(message);
    }
}