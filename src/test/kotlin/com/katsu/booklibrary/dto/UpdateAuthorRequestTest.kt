package com.katsu.booklibrary.dto

import com.katsu.booklibrary.support.rejectsInvalidArgs
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertDoesNotThrow
import java.time.LocalDate

class UpdateAuthorRequestTest {
    private val valid = UpdateAuthorRequest(name = "夏目 金之助")

    @Test
    fun `accepts partial fields`() {
        assertDoesNotThrow { UpdateAuthorRequest(name = "夏目 金之助") }
        assertDoesNotThrow { UpdateAuthorRequest(birthDate = LocalDate.of(1900, 1, 1)) }
    }

    @TestFactory
    fun `rejects invalid args`(): List<DynamicTest> {
        return rejectsInvalidArgs(
            "all fields null" to { UpdateAuthorRequest() },
            "blank name" to { valid.copy(name = "   ") },
            "birthDate in the future" to { valid.copy(birthDate = LocalDate.now().plusDays(1)) },
        )
    }
}
