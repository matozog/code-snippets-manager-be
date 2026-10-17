package com.snippets.manager.snippetsmanager.services;

import com.snippets.manager.snippetsmanager.models.CodeSnippet;
import com.snippets.manager.snippetsmanager.models.Tag;
import com.snippets.manager.snippetsmanager.repositories.CodeSnippetRepository;
import com.snippets.manager.snippetsmanager.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CodesSnippetService {

    @Autowired
    private CodeSnippetRepository codeSnippetRepository;

    @Autowired
    private TagRepository tagRepository;

    @Transactional
    public CodeSnippet saveSnippet(CodeSnippet codeSnippet) {
        System.out.println(codeSnippet);
        // one tag per name: duplicates from the request are merged, existing tags are reused
        Set<Tag> tagsAfterMapping = codeSnippet.getTags().stream()
                .collect(Collectors.toMap(Tag::getName, Function.identity(), (first, duplicate) -> first))
                .values().stream()
                .map(tag -> tagRepository.findByName(tag.getName()).orElse(tag))
                .collect(Collectors.toSet());
        codeSnippet.setTags(tagsAfterMapping);
        return codeSnippetRepository.save(codeSnippet);
    }

}
