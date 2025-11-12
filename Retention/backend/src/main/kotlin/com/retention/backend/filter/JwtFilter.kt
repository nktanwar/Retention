package com.retention.backend.filter

import com.retention.backend.utils.JwtUtil
import com.retention.backend.utils.UserPrincipal
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(
    private val jwtUtil: JwtUtil
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.requestURI

        // Skip authentication for public endpoints
        if (path.startsWith("/public")) {
            filterChain.doFilter(request, response)
            return
        }

        val authHeader = request.getHeader("Authorization")

        // If there’s no Authorization header or it’s not Bearer, skip the filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authHeader.substring(7) // remove "Bearer "
        val username = jwtUtil.extractusername(token) // ✅ correct function name casing

        val id = jwtUtil.extractid(token)

        // Only set authentication if not already present
        if ((username != null && id != null) && SecurityContextHolder.getContext().authentication == null) {
            if (jwtUtil.validateToken(token)) {
                // Create authentication token with empty authorities list
                val authToken = UsernamePasswordAuthenticationToken(
                    UserPrincipal(username, id), // principal
                    null,                        // credentials
                    listOf<SimpleGrantedAuthority>() // empty authorities list
                )
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)

                SecurityContextHolder.getContext().authentication = authToken
            }
        }

        filterChain.doFilter(request, response)
    }
}
