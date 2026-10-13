package com.example.banking.api.security

import com.example.banking.domain.exceptions.AuthenticationException
import com.example.banking.domain.service.AccessService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Converts a banking session token into the Spring Security identity used by
 * [RequireAccess] and Spring's AuthenticationPrincipal argument resolver.
 *
 * This filter only translates HTTP authentication into a domain [UserSession][com.example.banking.domain.model.UserSession].
 * Credential checks and session storage remain in [AccessService].
 */
class BearerSessionFilter(
    // Resolves an opaque bearer token to the application's domain session.
    private val accessService: AccessService,
    // Writes a consistent HTTP 401 response when a supplied token is invalid.
    private val authenticationEntryPoint: AuthenticationEntryPoint,
) : OncePerRequestFilter() {

    /**
     * Authenticates the request when it contains an Authorization header.
     *
     * Requests without the header continue anonymously. The selected controller's
     * [RequireAccess] annotation then decides whether authentication is required.
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        // Public endpoints may omit the header, while protected methods reject the
        // resulting anonymous request through method security.
        val authorization = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (authorization == null) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            // Validate the bearer token and load the customer represented by it.
            val userSession = accessService.authenticatedSession(authorization)

            // UserSession becomes the principal consumed by AuthenticationPrincipal.
            // Authorities are unnecessary because ownership is enforced by business services.
            val authentication = UsernamePasswordAuthenticationToken.authenticated(
                userSession,
                null,
                AuthorityUtils.NO_AUTHORITIES,
            )

            // Retain standard request metadata such as the remote address.
            authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

            // Publish the authentication for method security and controller arguments
            // during the rest of this request.
            val context = SecurityContextHolder.createEmptyContext()
            context.authentication = authentication
            SecurityContextHolder.setContext(context)
            filterChain.doFilter(request, response)
        } catch (exception: AuthenticationException) {
            // Never leave a partially authenticated context after validation fails.
            SecurityContextHolder.clearContext()

            // AuthenticationEntryPoint accepts Spring's exception type, so preserve
            // the safe domain message inside BadCredentialsException.
            authenticationEntryPoint.commence(
                request,
                response,
                BadCredentialsException(exception.message, exception),
            )
        }
    }
}
