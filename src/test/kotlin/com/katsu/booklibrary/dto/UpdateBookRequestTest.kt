package com.katsu.booklibrary.dto

import com.katsu.booklibrary.domain.Book.PublicationStatus
import com.katsu.booklibrary.support.rejectsInvalidArgs
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertDoesNotThrow

class UpdateBookRequestTest {
    private val valid = UpdateBookRequest(title = "新タイトル")

    @Test
    fun `accepts partial fields`() {
        assertDoesNotThrow { UpdateBookRequest(title = "新タイトル") }
        assertDoesNotThrow { UpdateBookRequest(price = 0) }
        assertDoesNotThrow { UpdateBookRequest(publicationStatus = PublicationStatus.PUBLISHED) }
        assertDoesNotThrow { UpdateBookRequest(authorIds = listOf(1)) }
    }

    @TestFactory
    fun `rejects invalid args`(): List<DynamicTest> {
        return rejectsInvalidArgs(
            "all fields null" to { UpdateBookRequest() },
            "blank title" to { valid.copy(title = "  ") },
            "price negative" to { valid.copy(price = -1) },
            "empty authorIds" to { valid.copy(authorIds = emptyList()) },
        )
    }
}
