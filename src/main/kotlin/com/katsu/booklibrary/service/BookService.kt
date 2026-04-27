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
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository,
) {
    @Transactional
    fun create(request: CreateBookRequest): Book {
        requireAuthorsExist(request.authorIds)
        return bookRepository.insert(request.title, request.price, request.publicationStatus, request.authorIds)
    }

    @Transactional
    fun update(
        id: Long,
        request: UpdateBookRequest,
    ): Book {
        val currentBook =
            bookRepository.findById(id)
                ?: throw NotFoundException("book $id not found")

        if (request.publicationStatus == PublicationStatus.UNPUBLISHED &&
            currentBook.publicationStatus == PublicationStatus.PUBLISHED
        ) {
            throw BusinessRuleException("cannot change publicationStatus from PUBLISHED to UNPUBLISHED")
        }
        request.authorIds?.let { requireAuthorsExist(it) }

        return bookRepository.update(
            id,
            request.title,
            request.price,
            request.publicationStatus,
            request.authorIds,
        )
    }

    @Transactional(readOnly = true)
    fun findByAuthor(authorId: Long): List<Book> {
        // 書籍 0 件の著者と、存在しない著者を区別するために事前確認する。
        if (authorRepository.findById(authorId) == null) {
            throw NotFoundException("author $authorId not found")
        }
        return bookRepository.findBooksByAuthorId(authorId)
    }

    private fun requireAuthorsExist(authorIds: List<Long>) {
        if (!authorRepository.existsAll(authorIds.toSet())) {
            throw UnknownAuthorException("some authorIds do not exist: $authorIds")
        }
    }
}
