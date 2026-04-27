package com.katsu.booklibrary.domain

data class Book(
    val id: Long,
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

    enum class PublicationStatus { UNPUBLISHED, PUBLISHED }
}
