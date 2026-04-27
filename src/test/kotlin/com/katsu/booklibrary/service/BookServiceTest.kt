package com.katsu.booklibrary.service

import com.katsu.booklibrary.domain.Book
import com.katsu.booklibrary.domain.Book.PublicationStatus
import com.katsu.booklibrary.dto.CreateBookRequest
import com.katsu.booklibrary.dto.UpdateBookRequest
import com.katsu.booklibrary.exception.BusinessRuleException
import com.katsu.booklibrary.exception.NotFoundException
import com.katsu.booklibrary.exception.UnknownAuthorException
import com.katsu.booklibrary.repository.AuthorRepository
import com.katsu.booklibrary.repository.BookRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class BookServiceTest(
    @MockK private val bookRepository: BookRepository,
    @MockK private val authorRepository: AuthorRepository,
) {
    private val sut = BookService(bookRepository, authorRepository)

    private fun book(
        id: Long = 1,
        status: PublicationStatus = PublicationStatus.PUBLISHED,
        authorIds: List<Long> = listOf(1),
    ) = Book(id, "猫", 800, status, authorIds)

    @Test
    fun `create throws UnknownAuthorException when some authorIds do not exist`() {
        every { authorRepository.existsAll(any()) } returns false
        assertThrows<UnknownAuthorException> {
            sut.create(CreateBookRequest("t", 100, PublicationStatus.UNPUBLISHED, listOf(1, 2)))
        }
    }

    @ParameterizedTest(name = "{0} -> {1} is allowed")
    @CsvSource(
        "PUBLISHED,    PUBLISHED",
        "UNPUBLISHED,  PUBLISHED",
        "UNPUBLISHED,  UNPUBLISHED",
    )
    fun `update allows publication status transition`(
        current: PublicationStatus,
        next: PublicationStatus,
    ) {
        val currentBook = book(status = current)
        every { bookRepository.findById(1) } returns currentBook
        every { bookRepository.update(1, any(), any(), next, any()) } returns currentBook.copy(publicationStatus = next)

        val result = sut.update(1, UpdateBookRequest(publicationStatus = next))

        assertEquals(next, result.publicationStatus)
    }

    @Test
    fun `update rejects PUBLISHED to UNPUBLISHED transition`() {
        every { bookRepository.findById(1) } returns book(status = PublicationStatus.PUBLISHED)

        assertThrows<BusinessRuleException> {
            sut.update(1, UpdateBookRequest(publicationStatus = PublicationStatus.UNPUBLISHED))
        }
    }

    @Test
    fun `update throws NotFoundException when id is missing`() {
        every { bookRepository.findById(1) } returns null
        assertThrows<NotFoundException> { sut.update(1, UpdateBookRequest(price = 500)) }
    }

    @Test
    fun `update throws UnknownAuthorException when authorIds reference missing authors`() {
        every { bookRepository.findById(1) } returns book(status = PublicationStatus.UNPUBLISHED)
        every { authorRepository.existsAll(any()) } returns false
        assertThrows<UnknownAuthorException> {
            sut.update(1, UpdateBookRequest(authorIds = listOf(999)))
        }
    }

    @Test
    fun `findByAuthor throws NotFoundException when author does not exist`() {
        every { authorRepository.findById(1) } returns null
        assertThrows<NotFoundException> { sut.findByAuthor(1) }
    }

    @Test
    fun `findByAuthor returns empty list when author exists but has no books`() {
        every { authorRepository.findById(1) } returns mockk(relaxed = true)
        every { bookRepository.findBooksByAuthorId(1) } returns emptyList()
        assertEquals(emptyList(), sut.findByAuthor(1))
    }
}
