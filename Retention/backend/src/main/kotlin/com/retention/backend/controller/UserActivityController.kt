package com.retention.backend.controller

import com.retention.backend.dto.DailyActivityDto
import com.retention.backend.repository.TestSessionRepository
import com.retention.backend.utils.UserPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import java.time.*
@RestController
@RequestMapping("/api/user/activity")
class UserActivityController(
    private val testSessionRepository: TestSessionRepository
) {

    @GetMapping
    fun getMonthlyTestActivity(
        year: Int?,
        month: Int?
    ): List<DailyActivityDto> {

        val auth = SecurityContextHolder.getContext().authentication
        val user = auth.principal as UserPrincipal
        val userId = user.id

        val now = LocalDate.now()
        val y = year ?: now.year
        val m = month ?: now.monthValue

        val zone = ZoneId.systemDefault()

        val startZdt = LocalDate.of(y, m, 1).atStartOfDay(zone)                 // ZonedDateTime
        val endZdt = startZdt.withDayOfMonth(startZdt.toLocalDate().lengthOfMonth()).plusDays(1)

        val startInstant = startZdt.toInstant()
        val endInstant = endZdt.toInstant()

        println("📊 [ACTIVITY] Query userId=$userId")
        println("📊 Range: $startZdt -> $endZdt")
        println("📊 Instants: $startInstant -> $endInstant")

        val sessions = testSessionRepository.findByUserIdAndSubmittedAtBetween(
            userId, startInstant, endInstant
        )

        println("📥 Sessions found: ${sessions.size}")
        sessions.forEach { println("  - submittedAt=${it.submittedAt}") }

        val grouped = sessions
            .filter { it.submittedAt != null }
            .groupBy { it.submittedAt!!.atZone(zone).toLocalDate() }

        return grouped.entries.map { (date, items) ->
            DailyActivityDto(date = date.toString(), count = items.size)
        }.sortedBy { it.date }
    }

}
