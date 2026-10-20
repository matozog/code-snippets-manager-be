package com.snippets.manager.snippetsmanager.services;

import com.snippets.manager.snippetsmanager.models.AppUser;
import com.snippets.manager.snippetsmanager.repositories.AppUserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;

    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    /**
     * Returns the user the token belongs to, creating it on the first sign-in and keeping email and name current.
     */
    public AppUser resolve(Jwt token) {
        String googleSub = token.getSubject();
        String email = token.getClaimAsString("email");
        String name = token.getClaimAsString("name");

        AppUser user = appUserRepository.findByGoogleSub(googleSub).orElseGet(() -> create(googleSub));
        if (!Objects.equals(user.getEmail(), email) || !Objects.equals(user.getName(), name)) {
            user.setEmail(email);
            user.setName(name);
            user = appUserRepository.save(user);
        }
        return user;
    }

    private AppUser create(String googleSub) {
        AppUser user = new AppUser();
        user.setGoogleSub(googleSub);
        try {
            return appUserRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            // parallel first requests of the same user: another request has just created it
            return appUserRepository.findByGoogleSub(googleSub).orElseThrow(() -> e);
        }
    }
}
