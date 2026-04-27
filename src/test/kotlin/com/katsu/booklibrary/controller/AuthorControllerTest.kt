package com.katsu.booklibrary.controller

import com.katsu.booklibrary.domain.Author
import com.katsu.booklibrary.domain.Book
import com.katsu.booklibrary.domain.Book.PublicationStatus
import com.katsu.booklibrary.dto.AuthorResponse
import com.katsu.booklibrary.dto.BookResponse
import com.katsu.booklibrary.dto.CreateAuthorRequest
import com.katsu.booklibrary.dto.UpdateAuthorRequest
import com.katsu.booklibrary.service.AuthorService
import com.katsu.booklibrary.service.BookService
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class AuthorControllerTest(
    @MockK private val authorService: AuthorService,
    @MockK private val bookService: BookService,
) {
    private val sut = AuthorController(authorService, bookService)

    @Test
    fun `create returns AuthorResponse from Service`() {
        val request = CreateAuthorRequest("夏目漱石", LocalDate.of(1867, 2, 9))
        every { authorService.create(request) } returns Author(1, "夏目漱石", LocalDate.of(1867, 2, 9))

        val response = sut.create(request)

        assertEquals(AuthorResponse(1, "夏目漱石", LocalDate.of(1867, 2, 9)), response)
    }

    @Test
    fun `update returns AuthorResponse from Service`() {
        val request = UpdateAuthorRequest(name = "夏目 金之助")
        every { authorService.update(1, request) } returns Author(1, "夏目 金之助", LocalDate.of(1867, 2, 9))

        val response = sut.update(1, request)

        assertEquals(AuthorResponse(1, "夏目 金之助", LocalDate.of(1867, 2, 9)), response)
    }

    @Test
    fun `booksByAuthor returns the list of BookResponse from Service`() {
        val book = Book(10, "猫", 800, PublicationStatus.PUBLISHED, listOf(1))
        every { bookService.findByAuthor(1) } returns listOf(book)

        val response = sut.booksByAuthor(1)

        assertEquals(
            listOf(BookResponse(10, "猫", 800, PublicationStatus.PUBLISHED, listOf(1))),
            response,
        )
    }
}
