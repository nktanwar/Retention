package com.retention.backend.dto

import com.retention.backend.model.Subject

data class PublicTestDto(
    val type : PublicTestType,
    val subject : List<Subject>


)

enum class PublicTestType {
    QUICKTEST,
    LONGTEST
}