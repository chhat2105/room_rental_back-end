package com.chhatkh.dev.security;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to inject the authenticated user in controller method parameters.
 *
 * Supported parameter types:
 * <ul>
 *     <li>{@link com.chhatkh.dev.entities.User} - fully loaded user entity</li>
 *     <li>{@link com.chhatkh.dev.security.CustomUserDetails} - authenticated user details</li>
 *     <li>{@link com.chhatkh.dev.dtos.UserDto} - user profile DTO</li>
 *     <li>{@link Long} - current user ID</li>
 * </ul>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal
@Parameter(hidden = true)
public @interface AuthUser {
}
