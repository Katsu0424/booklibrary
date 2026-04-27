package com.katsu.booklibrary.dto

import com.katsu.booklibrary.domain.Book.PublicationStatus

data class CreateBookRequest(
    val title: String,
    val price: Int,
    val publicationStatus: PublicationStatus,
    val authorIds: List<Long>,
) {
    init {
        require(title.isNotBlank()) { "title must not be blank" }
        require(price >= 0) { "price must be non-negative" }
        require(authorIds.isNotEmpty()) { "authorIds must not be empty" }
    }
}

/**
 * `null` のフィールドは「未指定 (= 変更しない)」を意味する。
 * 全フィールドが `null` の場合は 400 とする (空 PATCH は不正)。
 */
data class UpdateBookRequest(
    val title: String? = null,
    val price: Int? = null,
    val publicationStatus: PublicationStatus? = null,
    val authorIds: List<Long>? = null,
) {
    init {
        require(title != null || price != null || publicationStatus != null || authorIds != null) {
            "at least one field must be provided"
        }
        title?.let { require(it.isNotBlank()) { "title must not be blank" } }
        price?.let { require(it >= 0) { "price must be non-negative" } }
        authorIds?.let { require(it.isNotEmpty()) { "authorIds must not be empty" } }
    }
}
