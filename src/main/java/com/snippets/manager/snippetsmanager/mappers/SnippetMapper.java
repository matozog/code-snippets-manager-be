package com.snippets.manager.snippetsmanager.mappers;

import com.snippets.manager.snippetsmanager.dto.CodeSnippetRequest;
import com.snippets.manager.snippetsmanager.dto.CodeSnippetResponse;
import com.snippets.manager.snippetsmanager.dto.TagResponse;
import com.snippets.manager.snippetsmanager.models.CodeSnippet;
import com.snippets.manager.snippetsmanager.models.Tag;

import java.util.Comparator;

public final class SnippetMapper {

    private SnippetMapper() {
    }

    public static CodeSnippetResponse toResponse(CodeSnippet snippet) {
        return new CodeSnippetResponse(
                snippet.getIdSnippet(),
                snippet.getName(),
                snippet.getDescription(),
                snippet.getContent(),
                snippet.isFavorite(),
                snippet.getProgrammingLanguage(),
                snippet.getType(),
                snippet.getAddedFrom(),
                snippet.getAddedDate(),
                snippet.getTags().stream()
                        .sorted(Comparator.comparing(Tag::getName))
                        .map(SnippetMapper::toResponse)
                        .toList());
    }

    public static TagResponse toResponse(Tag tag) {
        return new TagResponse(tag.getIdTag(), tag.getName(), tag.getAddedDate());
    }

    /**
     * Copies the client-editable fields onto the entity; tags are resolved separately by the service.
     */
    public static void applyRequest(CodeSnippetRequest request, CodeSnippet snippet) {
        snippet.setName(request.name().strip());
        snippet.setDescription(request.description());
        snippet.setContent(request.content());
        snippet.setFavorite(Boolean.TRUE.equals(request.favorite()));
        snippet.setProgrammingLanguage(request.programmingLanguage());
        snippet.setType(request.type());
        snippet.setAddedFrom(request.addedFrom());
    }
}
