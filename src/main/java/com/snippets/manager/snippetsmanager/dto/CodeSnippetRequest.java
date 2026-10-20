package com.snippets.manager.snippetsmanager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Data a client may set on a snippet. Ids and dates are assigned by the server, so they are not part of the request.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CodeSnippetRequest(
        @NotBlank(message = "Name is required.")
        @Size(max = 255, message = "Name can have at most 255 characters.")
        String name,
        String description,
        String content,
        // boxed, so the field may be omitted: Jackson 3 rejects missing values for primitives
        Boolean favorite,
        @Size(max = 255, message = "Programming language can have at most 255 characters.")
        String programmingLanguage,
        @Size(max = 255, message = "Type can have at most 255 characters.")
        String type,
        @Size(max = 255, message = "Creation place can have at most 255 characters.")
        String addedFrom,
        List<@Valid TagRequest> tags) {
}
