package com.katsu.booklibrary.controller

import com.katsu.booklibrary.domain.Book
import com.katsu.booklibrary.domain.Book.PublicationStatus
import com.katsu.booklibrary.dto.BookResponse
import com.katsu.booklibrary.dto.CreateBookRequest
import com.katsu.booklibrary.dto.UpdateBookRequest
import com.katsu.booklibrary.service.BookService
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class BookControllerTest(
    @MockK private val bookService: BookService,
) {
    private val sut = BookController(bookService)

    @Test
    fun `create returns BookResponse from Service`() {
        val request = CreateBookRequest("猫", 800, PublicationStatus.PUBLISHED, listOf(1))
        every { bookService.create(request) } returns Book(10, "猫", 800, PublicationStatus.PUBLISHED, listOf(1))

        val response = sut.create(request)

        assertEquals(BookResponse(10, "猫", 800, PublicationStatus.PUBLISHED, listOf(1)), response)
    }

    @Test
    fun `update returns BookResponse from Service`() {
        val request = UpdateBookRequest(price = 900)
        every { bookService.update(10, request) } returns Book(10, "猫", 900, PublicationStatus.PUBLISHED, listOf(1))

        val response = sut.update(10, request)

        assertEquals(BookResponse(10, "猫", 900, PublicationStatus.PUBLISHED, listOf(1)), response)
    }
}
