package com.retention.backend.utils

import org.springframework.stereotype.Component
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.util.*
import io.jsonwebtoken.Claims

@Component
class JwtUtil {
    private val secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256) // auto-generate key
    private val jwtExpirationMs = 3600000


    fun generateToken(username : String, id : String) : String{
        val now = Date()
        val expiry = Date(now.time + jwtExpirationMs)

        val claims: MutableMap<String, Any> = HashMap()
        claims["id"] = id
        return Jwts.builder()
            .setSubject(username)
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(secretKey)
            .compact()

    }

    fun extractusername(token : String) : String? =
        extractAllClaims(token)?.subject

    fun extractid(token :String) : String? =
        extractAllClaims(token)?.get("id", String::class.java)

    fun validateToken(token : String) : Boolean {
        val claims = extractAllClaims(token)
        return claims != null && claims.expiration.after(Date())
    }


    private fun extractAllClaims(token : String) : Claims ? = try{
        Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body

    }catch (e: Exception){
        null
    }
}