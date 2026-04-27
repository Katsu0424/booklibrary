package com.katsu.booklibrary.domain

import com.katsu.booklibrary.support.rejectsInvalidArgs
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertDoesNotThrow
import java.time.LocalDate

class AuthorTest {
    private val valid = Author(1, "夏目漱石", LocalDate.of(1867, 2, 9))

    @Test
    fun `accepts a valid author`() {
        assertDoesNotThrow {
            Author(1, "夏目漱石", LocalDate.of(1867, 2, 9))
        }
    }

    @Test
    fun `accepts birthDate equal to today`() {
        assertDoesNotThrow { valid.copy(birthDate = LocalDate.now()) }
    }

    @TestFactory
    fun `rejects invalid args`(): List<DynamicTest> {
        return rejectsInvalidArgs(
            "blank name" to { valid.copy(name = "  ") },
            "birthDate in the future" to { valid.copy(birthDate = LocalDate.now().plusDays(1)) },
        )
    }
}
