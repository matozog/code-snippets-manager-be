package com.snippets.manager.snippetsmanager.dto;

import java.time.Instant;

public record TagResponse(Long idTag, String name, Instant addedDate) {
}
