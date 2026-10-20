package com.snippets.manager.snippetsmanager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TagRequest(
        @NotBlank(message = "Tag name is required.")
        @Size(max = 255, message = "Tag name can have at most 255 characters.")
        String name) {
}
