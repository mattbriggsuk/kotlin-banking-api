package com.example.banking.api.security

import org.springframework.security.access.prepost.PreAuthorize

/**
 * Requires a valid customer session before a controller class or method can execute.
 *
 * Keeping the Spring Security expression in this composed annotation gives controllers
 * a domain-specific marker and provides one place to change the access rule later.
 */
// The annotation can protect either every action on a controller or one selected action.
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
// Spring evaluates the annotation at runtime when it invokes the controller method.
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
// BearerSessionFilter supplies the authenticated principal evaluated by this expression.
@PreAuthorize("isAuthenticated()")
annotation class RequireAccess
