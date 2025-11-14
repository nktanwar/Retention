package com.retention.backend.utils

import com.retention.backend.config.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.spec.SecretKeySpec

@Component
class JwtUtil(
    private val jwtProperties: JwtProperties
) {
    private val jwtExpirationMs = jwtProperties.expirationMs

    init {
        println("🔐 JWT Secret (len=${jwtProperties.secret.length}): ${jwtProperties.secret.take(10)}...")
    }

    private fun getSigningKey(): SecretKeySpec {
        val keyBytes = jwtProperties.secret.trim().toByteArray(Charsets.UTF_8)
        // printing length helps debug mismatches
        println("🔐 getSigningKey -> keyBytes.length=${keyBytes.size}")
        return SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.jcaName)
    }

    fun generateToken(username: String, id: String): String {
        val now = Date()
        val expiry = Date(now.time + jwtExpirationMs)

        val claims: MutableMap<String, Any> = HashMap()
        claims["id"] = id
        claims["sub"] = username // ensure subject claim exists

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact()

    }

    fun extractusername(token: String): String? =
        extractAllClaims(token)?.subject

    fun extractid(token: String): String? =
        extractAllClaims(token)?.get("id", String::class.java)

    fun validateToken(token: String): Boolean {
        val claims = extractAllClaims(token)
        val valid = claims != null && claims.expiration.after(Date())
//        println("🔍 validateToken -> claimsPresent=${claims != null}, expired=${claims?.expiration?.before(Date())}")
        return valid
    }

    private fun extractAllClaims(token: String): Claims? = try {
        val key = getSigningKey()
//        println("🔍 extractAllClaims -> using keyAlg=${key.algorithm}, keyLen=${key.encoded.size}")
        Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    } catch (e: Exception) {
        println("❌ JWT parsing failed: ${e.message}")
        e.printStackTrace()
        null
    }
}
