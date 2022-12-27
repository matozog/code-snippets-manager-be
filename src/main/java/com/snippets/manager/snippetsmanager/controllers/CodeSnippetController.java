package com.snippets.manager.snippetsmanager.controllers;

import com.snippets.manager.snippetsmanager.exception.SnippetNotFoundException;
import com.snippets.manager.snippetsmanager.models.CodeSnippet;
import com.snippets.manager.snippetsmanager.repositories.CodeSnippetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/code-snippets")
public class CodeSnippetController {

    @Autowired
    private CodeSnippetRepository codeSnippetRepository;

    @GetMapping
    public Iterable findAll() {
        return codeSnippetRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CodeSnippet create(@RequestBody CodeSnippet codeSnippet) {
        return codeSnippetRepository.save(codeSnippet);
    }

    @GetMapping("/title/{bookTitle}")
    public List findByName(@PathVariable String bookTitle) {
        return codeSnippetRepository.findByName(bookTitle);
    }

    @GetMapping("/{id}")
    public CodeSnippet findOne(@PathVariable Long id) {
        return codeSnippetRepository.findById(id)
                .orElseThrow(SnippetNotFoundException::new);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        codeSnippetRepository.findById(id)
                .orElseThrow(SnippetNotFoundException::new);
        codeSnippetRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public CodeSnippet updateBook(@RequestBody CodeSnippet book, @PathVariable Long id) {
        codeSnippetRepository.findById(id)
                .orElseThrow(SnippetNotFoundException::new);
        return codeSnippetRepository.save(book);
    }
}
