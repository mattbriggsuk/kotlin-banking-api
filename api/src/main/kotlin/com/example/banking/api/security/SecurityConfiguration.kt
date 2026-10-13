package com.example.banking.api.security

import com.example.banking.domain.service.AccessService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter

/** Configures stateless bearer-session security for the HTTP API. */
@Configuration
// Activates PreAuthorize, including the expression composed into RequireAccess.
@EnableMethodSecurity
class SecurityConfiguration {
    /**
     * Builds the Spring Security filter chain used for every HTTP request.
     *
     * URL-level access remains open so controller annotations can state exactly which
     * operations need a session. The custom filter establishes the principal first.
     */
    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        accessService: AccessService,
        problemHandler: SecurityProblemHandler,
    ): SecurityFilterChain {
        // Construct the filter explicitly so it is registered once at a known position.
        val bearerSessionFilter = BearerSessionFilter(accessService, problemHandler)

        return http
            // Bearer-token API clients do not use browser cookies for authentication.
            .csrf(AbstractHttpConfigurer<*, *>::disable)
            // Stateless failures should return JSON rather than saving a browser redirect.
            .requestCache(AbstractHttpConfigurer<*, *>::disable)
            // Disable Spring's interactive and built-in authentication mechanisms.
            .formLogin(AbstractHttpConfigurer<*, *>::disable)
            .httpBasic(AbstractHttpConfigurer<*, *>::disable)
            .logout(AbstractHttpConfigurer<*, *>::disable)
            // Application sessions live in AccessService, never in an HTTP session.
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            // RequireAccess on controller methods supplies the endpoint access rules.
            .authorizeHttpRequests {
                it.anyRequest().permitAll()
            }
            // Security failures occur before controller advice and need a chain-level writer.
            .exceptionHandling {
                it.authenticationEntryPoint(problemHandler)
                    .accessDeniedHandler(problemHandler)
            }
            // Resolve bearer tokens before Spring assigns an anonymous identity.
            .addFilterBefore(bearerSessionFilter, AnonymousAuthenticationFilter::class.java)
            .build()
    }
}
