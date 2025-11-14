package com.retention.backend.filter

import com.retention.backend.utils.JwtUtil
import com.retention.backend.utils.UserPrincipal
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
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
        println("🧩 JwtFilter - Path: $path")

        if (path.startsWith("/public") || path.startsWith("/auth") || path.startsWith("/debug")) {
//            println("🟢 Skipping JWT filter for public path: $path")
            filterChain.doFilter(request, response)
            return
        }

        val authHeader = request.getHeader("Authorization")
//        println("🧩 Authorization header: ${authHeader?.let { it.take(200) } ?: "null"}")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            println("🚫 No bearer token found.")
            filterChain.doFilter(request, response)
            return
        }

        val token = authHeader.substring(7)
//        println("🧩 Token (head): ${token.take(30)}...")

        val username = jwtUtil.extractusername(token)
        val id = jwtUtil.extractid(token)
        println("🧩 Extracted username=$username, id=$id")

        if ((username != null && id != null) && SecurityContextHolder.getContext().authentication == null) {
            val valid = try {
                jwtUtil.validateToken(token)
            } catch (e: Exception) {
//                println("❌ Exception during jwtUtil.validateToken: ${e.message}")
                false
            }
//            println("🧩 Token valid? $valid")
            if (valid) {
                // IMPORTANT: if your controllers need role 'USER', give that authority
                val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
                val authToken = UsernamePasswordAuthenticationToken(
                    UserPrincipal(username, id),
                    null,
                    authorities
                )
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authToken
//                println("✅ Authentication set in SecurityContext with authorities=${authorities.map { it.authority }}")
            } else {
                println("❌ Token validation failed.")
            }
        } else {
//            println("ℹ️ No username/id or authentication already present.")
        }

        filterChain.doFilter(request, response)
    }
}
