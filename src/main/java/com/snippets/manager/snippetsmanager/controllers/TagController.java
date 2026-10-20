package com.snippets.manager.snippetsmanager.controllers;

import com.snippets.manager.snippetsmanager.dto.TagRequest;
import com.snippets.manager.snippetsmanager.dto.TagResponse;
import com.snippets.manager.snippetsmanager.mappers.SnippetMapper;
import com.snippets.manager.snippetsmanager.models.AppUser;
import com.snippets.manager.snippetsmanager.models.Tag;
import com.snippets.manager.snippetsmanager.repositories.TagRepository;
import com.snippets.manager.snippetsmanager.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/tag")
public class TagController {

    private final TagRepository tagRepository;

    public TagController(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @GetMapping
    public List<TagResponse> findAll(@CurrentUser AppUser user) {
        return tagRepository.findAllByOwnerIdOrderByName(user.getId()).stream().map(SnippetMapper::toResponse).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TagResponse create(@Valid @RequestBody TagRequest request, @CurrentUser AppUser user) {
        String name = request.name().strip();
        if (tagRepository.findByOwnerIdAndName(user.getId(), name).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tag '" + name + "' already exists.");
        }
        Tag tag = new Tag();
        tag.setName(name);
        tag.setOwner(user);
        return SnippetMapper.toResponse(tagRepository.save(tag));
    }
}
