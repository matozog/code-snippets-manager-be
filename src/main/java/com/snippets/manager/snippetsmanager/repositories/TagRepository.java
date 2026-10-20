package com.snippets.manager.snippetsmanager.repositories;

import com.snippets.manager.snippetsmanager.models.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Tags are private to their owner, so every query is scoped to the owner.
 */
@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {

    List<Tag> findAllByOwnerIdOrderByName(Long ownerId);

    Optional<Tag> findByOwnerIdAndName(Long ownerId, String name);
}
