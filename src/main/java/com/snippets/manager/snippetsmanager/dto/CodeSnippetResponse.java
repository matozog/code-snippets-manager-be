package com.snippets.manager.snippetsmanager.dto;

import java.time.Instant;
import java.util.List;

public record CodeSnippetResponse(
        Long idSnippet,
        String name,
        String description,
        String content,
        boolean favorite,
        String programmingLanguage,
        String type,
        String addedFrom,
        Instant addedDate,
        List<TagResponse> tags) {
}
