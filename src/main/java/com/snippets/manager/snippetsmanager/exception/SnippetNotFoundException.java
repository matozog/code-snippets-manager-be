package com.snippets.manager.snippetsmanager.exception;

public class SnippetNotFoundException extends RuntimeException {

    public SnippetNotFoundException(Long id) {
        super("Snippet " + id + " not found.");
    }
}
