package com.katsu.booklibrary.dto

import java.time.LocalDate

data class CreateAuthorRequest(
    val name: String,
    val birthDate: LocalDate,
) {
    init {
        require(name.isNotBlank()) { "name must not be blank" }
        require(!birthDate.isAfter(LocalDate.now())) { "birthDate must be today or in the past" }
    }
}

/**
 * `null` のフィールドは「未指定 (= 変更しない)」を意味する。
 * 全フィールドが `null` の場合は 400 とする (空 PATCH は不正)。
 */
data class UpdateAuthorRequest(
    val name: String? = null,
    val birthDate: LocalDate? = null,
) {
    init {
        require(name != null || birthDate != null) {
            "at least one field must be provided"
        }
        name?.let { require(it.isNotBlank()) { "name must not be blank" } }
        birthDate?.let { require(!it.isAfter(LocalDate.now())) { "birthDate must be today or in the past" } }
    }
}
