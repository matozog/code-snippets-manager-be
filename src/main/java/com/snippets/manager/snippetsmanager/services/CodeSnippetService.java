package com.snippets.manager.snippetsmanager.services;

import com.snippets.manager.snippetsmanager.dto.CodeSnippetRequest;
import com.snippets.manager.snippetsmanager.dto.CodeSnippetResponse;
import com.snippets.manager.snippetsmanager.dto.TagRequest;
import com.snippets.manager.snippetsmanager.exception.SnippetNotFoundException;
import com.snippets.manager.snippetsmanager.mappers.SnippetMapper;
import com.snippets.manager.snippetsmanager.models.AppUser;
import com.snippets.manager.snippetsmanager.models.CodeSnippet;
import com.snippets.manager.snippetsmanager.models.Tag;
import com.snippets.manager.snippetsmanager.repositories.CodeSnippetRepository;
import com.snippets.manager.snippetsmanager.repositories.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Snippets are private: every operation works only on snippets of the given owner.
 */
@Service
@Transactional
public class CodeSnippetService {

    private final CodeSnippetRepository codeSnippetRepository;
    private final TagRepository tagRepository;

    public CodeSnippetService(CodeSnippetRepository codeSnippetRepository, TagRepository tagRepository) {
        this.codeSnippetRepository = codeSnippetRepository;
        this.tagRepository = tagRepository;
    }

    @Transactional(readOnly = true)
    public List<CodeSnippetResponse> findAll(AppUser owner) {
        return codeSnippetRepository.findAllByOwnerId(owner.getId()).stream().map(SnippetMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CodeSnippetResponse findById(Long id, AppUser owner) {
        return SnippetMapper.toResponse(getSnippet(id, owner));
    }

    public CodeSnippetResponse create(CodeSnippetRequest request, AppUser owner) {
        CodeSnippet snippet = new CodeSnippet();
        snippet.setOwner(owner);
        SnippetMapper.applyRequest(request, snippet);
        snippet.setTags(resolveTags(request.tags(), owner));
        return SnippetMapper.toResponse(codeSnippetRepository.save(snippet));
    }

    public CodeSnippetResponse update(Long id, CodeSnippetRequest request, AppUser owner) {
        CodeSnippet snippet = getSnippet(id, owner);
        SnippetMapper.applyRequest(request, snippet);
        snippet.setTags(resolveTags(request.tags(), owner));
        return SnippetMapper.toResponse(snippet);
    }

    public void delete(Long id, AppUser owner) {
        codeSnippetRepository.delete(getSnippet(id, owner));
    }

    // another user's snippet is reported as not found, so its existence is not revealed
    private CodeSnippet getSnippet(Long id, AppUser owner) {
        return codeSnippetRepository.findByIdSnippetAndOwnerId(id, owner.getId())
                .orElseThrow(() -> new SnippetNotFoundException(id));
    }

    // tags are resolved by name within the owner's tags: duplicates are merged, existing ones reused, missing created
    private Set<Tag> resolveTags(List<TagRequest> tags, AppUser owner) {
        if (tags == null) return new HashSet<>();

        return tags.stream()
                .filter(Objects::nonNull)
                .map(TagRequest::name)
                .map(String::strip)
                .distinct()
                .map(name -> tagRepository.findByOwnerIdAndName(owner.getId(), name)
                        .orElseGet(() -> tagRepository.save(newTag(name, owner))))
                .collect(Collectors.toCollection(HashSet::new));
    }

    private static Tag newTag(String name, AppUser owner) {
        Tag tag = new Tag();
        tag.setName(name);
        tag.setOwner(owner);
        return tag;
    }

}
