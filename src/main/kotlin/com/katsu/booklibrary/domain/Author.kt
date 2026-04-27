package com.katsu.booklibrary.domain

import java.time.LocalDate

data class Author(
    val id: Long,
    val name: String,
    val birthDate: LocalDate,
) {
    init {
        require(name.isNotBlank()) { "name must not be blank" }
        require(!birthDate.isAfter(LocalDate.now())) { "birthDate must be today or in the past" }
    }
}
