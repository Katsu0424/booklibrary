package com.katsu.booklibrary.domain

import com.katsu.booklibrary.domain.Book.PublicationStatus
import com.katsu.booklibrary.support.rejectsInvalidArgs
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertDoesNotThrow

class BookTest {
    private val valid = Book(1, "吾輩は猫である", 800, PublicationStatus.PUBLISHED, listOf(1))

    @Test
    fun `accepts a valid book`() {
        assertDoesNotThrow {
            Book(1, "吾輩は猫である", 800, PublicationStatus.PUBLISHED, listOf(1))
        }
    }

    @Test
    fun `accepts price equal to zero`() {
        assertDoesNotThrow { valid.copy(price = 0) }
    }

    @TestFactory
    fun `rejects invalid args`(): List<DynamicTest> {
        return rejectsInvalidArgs(
            "blank title" to { valid.copy(title = "  ") },
            "price negative" to { valid.copy(price = -1) },
            "empty authorIds" to { valid.copy(authorIds = emptyList()) },
        )
    }
}
