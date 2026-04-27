package com.katsu.booklibrary.repository

import com.katsu.booklibrary.support.PostgresContainerBase
import org.junit.jupiter.api.Test
import org.springframework.boot.jooq.test.autoconfigure.JooqTest
import org.springframework.context.annotation.Import
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@JooqTest
@Import(AuthorRepository::class)
class AuthorRepositoryTest(
    private val sut: AuthorRepository,
) : PostgresContainerBase() {
    @Test
    fun `insert then findById returns the same author`() {
        val inserted = sut.insert("夏目漱石", LocalDate.of(1867, 2, 9))
        val found = assertNotNull(sut.findById(inserted.id))
        assertEquals(inserted, found)
    }

    @Test
    fun `findById returns null for missing id`() {
        assertNull(sut.findById(999_999))
    }

    @Test
    fun `existsAll true when all ids exist`() {
        val a = sut.insert("A", LocalDate.of(1900, 1, 1))
        val b = sut.insert("B", LocalDate.of(1901, 2, 2))
        assertTrue(sut.existsAll(setOf(a.id, b.id)))
    }

    @Test
    fun `existsAll false when some id is missing`() {
        val a = sut.insert("A", LocalDate.of(1900, 1, 1))
        assertFalse(sut.existsAll(setOf(a.id, 999_999)))
    }

    @Test
    fun `existsAll true when set is empty`() {
        assertTrue(sut.existsAll(emptySet()))
    }

    @Test
    fun `update name only changes name`() {
        val a = sut.insert("A", LocalDate.of(1900, 1, 1))
        val result = sut.update(a.id, name = "A2", birthDate = null)
        assertEquals("A2", result.name)
        assertEquals(a.birthDate, result.birthDate)
    }
}
