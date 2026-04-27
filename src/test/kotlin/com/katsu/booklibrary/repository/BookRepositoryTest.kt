package com.katsu.booklibrary.repository

import com.katsu.booklibrary.domain.Book.PublicationStatus
import com.katsu.booklibrary.support.PostgresContainerBase
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.boot.jooq.test.autoconfigure.JooqTest
import org.springframework.context.annotation.Import
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@JooqTest
@Import(BookRepository::class, AuthorRepository::class)
class BookRepositoryTest(
    private val sut: BookRepository,
    private val authorRepository: AuthorRepository,
) : PostgresContainerBase() {
    private fun author(name: String = "A"): Long = authorRepository.insert(name, LocalDate.of(1900, 1, 1)).id

    @Test
    fun `insert then findById returns the same book with sorted authorIds`() {
        val a1 = author("A1")
        val a2 = author("A2")
        val inserted = sut.insert("猫", 800, PublicationStatus.PUBLISHED, listOf(a2, a1))
        val found = assertNotNull(sut.findById(inserted.id))
        assertEquals("猫", found.title)
        assertEquals(800, found.price)
        assertEquals(PublicationStatus.PUBLISHED, found.publicationStatus)
        assertEquals(listOf(a1, a2).sorted(), found.authorIds)
    }

    @Test
    fun `insert deduplicates authorIds`() {
        val a = author()
        val inserted = sut.insert("t", 100, PublicationStatus.UNPUBLISHED, listOf(a, a))
        assertEquals(listOf(a), assertNotNull(sut.findById(inserted.id)).authorIds)
    }

    @ParameterizedTest(name = "price {0} round trips")
    @ValueSource(ints = [0, Int.MAX_VALUE])
    fun `price boundary values round trip`(price: Int) {
        val a = author()
        val inserted = sut.insert("t", price, PublicationStatus.UNPUBLISHED, listOf(a))
        assertEquals(price, assertNotNull(sut.findById(inserted.id)).price)
    }

    @Test
    fun `findBooksByAuthorId returns all books authored by the given author`() {
        val a1 = author("A1")
        val a2 = author("A2")
        val b1 = sut.insert("B1", 100, PublicationStatus.PUBLISHED, listOf(a1))
        val b2 = sut.insert("B2", 200, PublicationStatus.PUBLISHED, listOf(a1, a2))
        sut.insert("B3", 300, PublicationStatus.PUBLISHED, listOf(a2))
        val result = sut.findBooksByAuthorId(a1)
        assertEquals(listOf(b1.id, b2.id).sorted(), result.map { it.id }.sorted())
        val b2Found = assertNotNull(result.firstOrNull { it.id == b2.id })
        assertEquals(listOf(a1, a2).sorted(), b2Found.authorIds)
    }

    @Test
    fun `findBooksByAuthorId returns empty list when author has no books`() {
        val a = author()
        assertTrue(sut.findBooksByAuthorId(a).isEmpty())
    }

    @Test
    fun `update replaces the author set when authorIds is given`() {
        val a1 = author("A1")
        val a2 = author("A2")
        val a3 = author("A3")
        val b = sut.insert("B", 100, PublicationStatus.PUBLISHED, listOf(a1, a2))
        val result = sut.update(b.id, null, null, null, listOf(a3))
        assertEquals(listOf(a3), result.authorIds)
    }

    @Test
    fun `update changes scalar fields`() {
        val a = author()
        val b = sut.insert("old", 100, PublicationStatus.UNPUBLISHED, listOf(a))
        val result = sut.update(b.id, title = "new", price = 500, status = PublicationStatus.PUBLISHED, authorIds = null)
        assertEquals("new", result.title)
        assertEquals(500, result.price)
        assertEquals(PublicationStatus.PUBLISHED, result.publicationStatus)
        assertEquals(listOf(a), result.authorIds)
    }
}
