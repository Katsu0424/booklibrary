package com.katsu.booklibrary.service

import com.katsu.booklibrary.domain.Author
import com.katsu.booklibrary.dto.UpdateAuthorRequest
import com.katsu.booklibrary.exception.NotFoundException
import com.katsu.booklibrary.repository.AuthorRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class AuthorServiceTest(
    @MockK private val authorRepository: AuthorRepository,
) {
    private val sut = AuthorService(authorRepository)

    @Test
    fun `update throws NotFoundException when id is missing`() {
        every { authorRepository.findById(1) } returns null
        assertThrows<NotFoundException> { sut.update(1, UpdateAuthorRequest(name = "X")) }
    }

    @Test
    fun `update delegates to Repository when id exists`() {
        val current = Author(1, "A", LocalDate.of(1900, 1, 1))
        val updated = current.copy(name = "A2")
        every { authorRepository.findById(1) } returns current
        every { authorRepository.update(1, "A2", null) } returns updated
        assertEquals(updated, sut.update(1, UpdateAuthorRequest(name = "A2")))
    }
}
