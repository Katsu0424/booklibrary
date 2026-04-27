package com.katsu.booklibrary.exception

import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.mock.web.MockHttpServletRequest
import kotlin.test.assertEquals

class GlobalExceptionHandlerTest {
    private val sut = GlobalExceptionHandler()

    @Test
    fun `handleUnreadable surfaces nested IllegalArgumentException message`() {
        val request = ServletServerHttpRequest(MockHttpServletRequest())
        val cause = IllegalArgumentException("price must be non-negative")
        val exception = HttpMessageNotReadableException("malformed", cause, request)

        val problemDetail = sut.handleUnreadable(exception)

        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.status)
        assertEquals("price must be non-negative", problemDetail.detail)
    }

    @Test
    fun `handleIllegalArgument returns 400 with the exception message`() {
        val exception = IllegalArgumentException("title must not be blank")

        val problemDetail = sut.handleIllegalArgument(exception)

        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.status)
        assertEquals("title must not be blank", problemDetail.detail)
    }
}
