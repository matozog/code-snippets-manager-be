package com.snippets.manager.snippetsmanager.controllers;

import com.snippets.manager.snippetsmanager.dto.CodeSnippetRequest;
import com.snippets.manager.snippetsmanager.dto.CodeSnippetResponse;
import com.snippets.manager.snippetsmanager.models.AppUser;
import com.snippets.manager.snippetsmanager.security.CurrentUser;
import com.snippets.manager.snippetsmanager.services.CodeSnippetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/code-snippets")
public class CodeSnippetController {

    private final CodeSnippetService codeSnippetService;

    public CodeSnippetController(CodeSnippetService codeSnippetService) {
        this.codeSnippetService = codeSnippetService;
    }

    @GetMapping
    public List<CodeSnippetResponse> findAll(@CurrentUser AppUser user) {
        return codeSnippetService.findAll(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CodeSnippetResponse create(@Valid @RequestBody CodeSnippetRequest request, @CurrentUser AppUser user) {
        return codeSnippetService.create(request, user);
    }

    @GetMapping("/{id}")
    public CodeSnippetResponse findOne(@PathVariable Long id, @CurrentUser AppUser user) {
        return codeSnippetService.findById(id, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @CurrentUser AppUser user) {
        codeSnippetService.delete(id, user);
    }

    @PutMapping("/{id}")
    public CodeSnippetResponse updateSnippet(@Valid @RequestBody CodeSnippetRequest request, @PathVariable Long id,
                                             @CurrentUser AppUser user) {
        return codeSnippetService.update(id, request, user);
    }
}
