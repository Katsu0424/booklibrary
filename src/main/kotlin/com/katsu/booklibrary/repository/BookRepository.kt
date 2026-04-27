package com.katsu.booklibrary.repository

import com.katsu.booklibrary.domain.Book
import com.katsu.booklibrary.domain.Book.PublicationStatus
import com.katsu.booklibrary.jooq.tables.references.BOOKS
import com.katsu.booklibrary.jooq.tables.references.BOOK_AUTHORS
import org.jooq.DSLContext
import org.jooq.impl.DSL.row
import org.springframework.stereotype.Repository
import com.katsu.booklibrary.jooq.enums.PublicationStatus as JooqStatus

@Repository
class BookRepository(
    private val dsl: DSLContext,
) {
    /**
     * 新規書籍を INSERT し、続けて中間テーブルへ著者を関連付ける。
     *
     * - `authorIds` は重複排除した上で関連付ける
     * - 戻り値の `authorIds` は昇順ソート済み
     */
    fun insert(
        title: String,
        price: Int,
        status: PublicationStatus,
        authorIds: List<Long>,
    ): Book {
        val record =
            dsl
                .insertInto(BOOKS)
                .set(BOOKS.TITLE, title)
                .set(BOOKS.PRICE, price)
                .set(BOOKS.PUBLICATION_STATUS, status.toJooq())
                .returning(BOOKS.ID)
                .fetchSingle()
        val bookId = checkNotNull(record.id)
        val distinctAuthorIds = authorIds.distinct()
        bulkInsertBookAuthors(bookId, distinctAuthorIds)
        return Book(bookId, title, price, status, distinctAuthorIds.sorted())
    }

    /**
     * 指定 id の書籍を著者一覧と共に返す。存在しない場合は `null`。
     */
    fun findById(id: Long): Book? {
        val record =
            dsl
                .selectFrom(BOOKS)
                .where(BOOKS.ID.eq(id))
                .fetchOne()
                ?: return null
        val authorIds = fetchAuthorIds(listOf(id))[id] ?: emptyList()
        return Book(
            id = checkNotNull(record.id),
            title = record.title,
            price = record.price,
            publicationStatus = record.publicationStatus.toDomain(),
            authorIds = authorIds,
        )
    }

    /**
     * 部分更新。非 null のフィールドだけを SET し、`authorIds` 非 null なら中間テーブルを再構築する。
     *
     * 前提条件:
     * - id は呼び出し側で存在確認済みであること (= Service が `findById` で 404 を判定済み)
     * - 4 引数のうち少なくとも 1 つは非 null (= DTO で空 PATCH を 400 として弾き済み)
     */
    fun update(
        id: Long,
        title: String?,
        price: Int?,
        status: PublicationStatus?,
        authorIds: List<Long>?,
    ): Book {
        dsl
            .updateQuery(BOOKS)
            .apply {
                title?.let { addValue(BOOKS.TITLE, it) }
                price?.let { addValue(BOOKS.PRICE, it) }
                status?.let { addValue(BOOKS.PUBLICATION_STATUS, it.toJooq()) }
                addConditions(BOOKS.ID.eq(id))
            }.execute()

        if (authorIds != null) {
            dsl.deleteFrom(BOOK_AUTHORS).where(BOOK_AUTHORS.BOOK_ID.eq(id)).execute()
            bulkInsertBookAuthors(id, authorIds.distinct())
        }
        return checkNotNull(findById(id)) { "book $id disappeared during update" }
    }

    /**
     * 指定著者が執筆した全書籍を返す。共著者を持つ書籍はその共著者 id も `Book.authorIds` に含まれる。
     */
    fun findBooksByAuthorId(authorId: Long): List<Book> {
        val bookIds =
            dsl
                .selectDistinct(BOOK_AUTHORS.BOOK_ID)
                .from(BOOK_AUTHORS)
                .where(BOOK_AUTHORS.AUTHOR_ID.eq(authorId))
                .orderBy(BOOK_AUTHORS.BOOK_ID.asc())
                .fetch()
                .mapNotNull { it.get(BOOK_AUTHORS.BOOK_ID) }
        if (bookIds.isEmpty()) return emptyList()
        val books =
            dsl
                .selectFrom(BOOKS)
                .where(BOOKS.ID.`in`(bookIds))
                .orderBy(BOOKS.ID.asc())
                .fetch()
        val authorIdsByBookId = fetchAuthorIds(bookIds)
        return books.map { record ->
            val bookId = checkNotNull(record.id)
            Book(
                id = bookId,
                title = record.title,
                price = record.price,
                publicationStatus = record.publicationStatus.toDomain(),
                authorIds = authorIdsByBookId[bookId] ?: emptyList(),
            )
        }
    }

    /**
     * 複数 bookId に対する `bookId → authorId のリスト` のマップを 1 クエリで取得する。
     */
    private fun fetchAuthorIds(bookIds: List<Long>): Map<Long, List<Long>> {
        if (bookIds.isEmpty()) return emptyMap()
        return dsl
            .selectFrom(BOOK_AUTHORS)
            .where(BOOK_AUTHORS.BOOK_ID.`in`(bookIds))
            .orderBy(BOOK_AUTHORS.BOOK_ID.asc(), BOOK_AUTHORS.AUTHOR_ID.asc())
            .fetch()
            .groupBy({ it.bookId }, { it.authorId })
    }

    private fun bulkInsertBookAuthors(
        bookId: Long,
        authorIds: List<Long>,
    ) {
        if (authorIds.isEmpty()) return
        dsl
            .insertInto(BOOK_AUTHORS, BOOK_AUTHORS.BOOK_ID, BOOK_AUTHORS.AUTHOR_ID)
            .valuesOfRows(authorIds.map { row(bookId, it) })
            .execute()
    }

    private fun PublicationStatus.toJooq(): JooqStatus {
        return JooqStatus.valueOf(this.name)
    }

    private fun JooqStatus.toDomain(): PublicationStatus {
        return PublicationStatus.valueOf(this.name)
    }
}
