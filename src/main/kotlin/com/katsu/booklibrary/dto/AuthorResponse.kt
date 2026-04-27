package com.katsu.booklibrary.dto

import com.katsu.booklibrary.domain.Author
import java.time.LocalDate

data class AuthorResponse(
    val id: Long,
    val name: String,
    val birthDate: LocalDate,
) {
    companion object {
        fun from(author: Author): AuthorResponse {
            return AuthorResponse(author.id, author.name, author.birthDate)
        }
    }
}
