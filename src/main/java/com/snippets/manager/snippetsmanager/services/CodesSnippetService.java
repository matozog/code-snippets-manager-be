package com.snippets.manager.snippetsmanager.services;

import com.snippets.manager.snippetsmanager.models.CodeSnippet;
import com.snippets.manager.snippetsmanager.models.Tag;
import com.snippets.manager.snippetsmanager.repositories.CodeSnippetRepository;
import com.snippets.manager.snippetsmanager.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
        List<Tag> tags = tagRepository.findAll();
        Set<Tag> tagsAfterMapping = codeSnippet.getTags().stream().map(tag -> {
            Optional<Tag> tagInDB = tags.stream().filter(tag1 -> tag1.getName().equals(tag.getName())).findFirst();
            tagInDB.ifPresent(value -> tag.setIdTag(value.getIdTag()));
            return tag;
        }).collect(Collectors.toSet());
        codeSnippet.setTags(tagsAfterMapping);
        return codeSnippetRepository.save(codeSnippet);
    }

}
