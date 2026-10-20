package com.snippets.manager.snippetsmanager.repositories;

import com.snippets.manager.snippetsmanager.models.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends CrudRepository<AppUser, Long> {

    Optional<AppUser> findByGoogleSub(String googleSub);
}
