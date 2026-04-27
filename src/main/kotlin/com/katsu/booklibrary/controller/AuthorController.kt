package com.katsu.booklibrary.controller

import com.katsu.booklibrary.dto.AuthorResponse
import com.katsu.booklibrary.dto.BookResponse
import com.katsu.booklibrary.dto.CreateAuthorRequest
import com.katsu.booklibrary.dto.UpdateAuthorRequest
import com.katsu.booklibrary.service.AuthorService
import com.katsu.booklibrary.service.BookService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/authors")
class AuthorController(
    private val authorService: AuthorService,
    private val bookService: BookService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody request: CreateAuthorRequest,
    ): AuthorResponse {
        return AuthorResponse.from(authorService.create(request))
    }

    @PatchMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody request: UpdateAuthorRequest,
    ): AuthorResponse {
        return AuthorResponse.from(authorService.update(id, request))
    }

    @GetMapping("/{id}/books")
    fun booksByAuthor(
        @PathVariable id: Long,
    ): List<BookResponse> {
        return bookService.findByAuthor(id).map { BookResponse.from(it) }
    }
}
