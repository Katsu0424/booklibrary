package com.katsu.booklibrary.controller

import com.katsu.booklibrary.dto.BookResponse
import com.katsu.booklibrary.dto.CreateBookRequest
import com.katsu.booklibrary.dto.UpdateBookRequest
import com.katsu.booklibrary.service.BookService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/books")
class BookController(
    private val bookService: BookService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody request: CreateBookRequest,
    ): BookResponse {
        return BookResponse.from(bookService.create(request))
    }

    @PatchMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody request: UpdateBookRequest,
    ): BookResponse {
        return BookResponse.from(bookService.update(id, request))
    }
}
