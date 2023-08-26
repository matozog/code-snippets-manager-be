package com.snippets.manager.snippetsmanager.exception;

public class SnippetNotFoundException extends RuntimeException {

    public SnippetNotFoundException() {
        super();
    }

    public SnippetNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
