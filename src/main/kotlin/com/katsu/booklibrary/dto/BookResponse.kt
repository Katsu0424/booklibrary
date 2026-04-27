package com.katsu.booklibrary.dto

import com.katsu.booklibrary.domain.Book
import com.katsu.booklibrary.domain.Book.PublicationStatus

data class BookResponse(
    val id: Long,
    val title: String,
    val price: Int,
    val publicationStatus: PublicationStatus,
    val authorIds: List<Long>,
) {
    companion object {
        fun from(book: Book): BookResponse {
            return BookResponse(book.id, book.title, book.price, book.publicationStatus, book.authorIds)
        }
    }
}
