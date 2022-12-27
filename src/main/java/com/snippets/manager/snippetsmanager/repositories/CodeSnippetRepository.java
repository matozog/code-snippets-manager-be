package com.snippets.manager.snippetsmanager.repositories;

import com.snippets.manager.snippetsmanager.models.CodeSnippet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeSnippetRepository extends CrudRepository<CodeSnippet, Long> {
    List<CodeSnippet> findByName(String title);
}
