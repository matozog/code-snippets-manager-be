package com.snippets.manager.snippetsmanager.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Injects the signed-in {@link com.snippets.manager.snippetsmanager.models.AppUser} into a controller method;
 * the user is created on the first request.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser {
}
