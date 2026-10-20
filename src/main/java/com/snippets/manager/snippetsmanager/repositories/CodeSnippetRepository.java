package com.snippets.manager.snippetsmanager.repositories;

import com.snippets.manager.snippetsmanager.models.CodeSnippet;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Every query is scoped to the owner, so one user can never read or change another user's snippets.
 */
@Repository
public interface CodeSnippetRepository extends CrudRepository<CodeSnippet, Long> {

    // tags are always serialized with snippets, so they are fetched in the same query
    @EntityGraph(attributePaths = "tags")
    List<CodeSnippet> findAllByOwnerId(Long ownerId);

    Optional<CodeSnippet> findByIdSnippetAndOwnerId(Long idSnippet, Long ownerId);
}
