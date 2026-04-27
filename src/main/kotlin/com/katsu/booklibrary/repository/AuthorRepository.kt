package com.katsu.booklibrary.repository

import com.katsu.booklibrary.domain.Author
import com.katsu.booklibrary.jooq.tables.references.AUTHORS
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class AuthorRepository(
    private val dsl: DSLContext,
) {
    fun insert(
        name: String,
        birthDate: LocalDate,
    ): Author {
        val record =
            dsl
                .insertInto(AUTHORS)
                .set(AUTHORS.NAME, name)
                .set(AUTHORS.BIRTH_DATE, birthDate)
                .returning(AUTHORS.ID)
                .fetchSingle()
        return Author(checkNotNull(record.id), name, birthDate)
    }

    fun findById(id: Long): Author? {
        return dsl
            .selectFrom(AUTHORS)
            .where(AUTHORS.ID.eq(id))
            .fetchOne()
            ?.let { Author(checkNotNull(it.id), it.name, it.birthDate) }
    }

    /**
     * 与えられた著者 id 群が **全て** `authors` に存在するか判定する。
     */
    fun existsAll(authorIds: Set<Long>): Boolean {
        if (authorIds.isEmpty()) {
            return true
        }
        val count =
            dsl.fetchCount(
                AUTHORS,
                AUTHORS.ID.`in`(authorIds),
            )
        return count == authorIds.size
    }

    /**
     * 部分更新。`name` / `birthDate` のうち非 null のものだけを SET する。
     *
     * 前提条件:
     * - id は呼び出し側で存在確認済みであること (= Service が `findById` で 404 を判定済み)
     * - `name` / `birthDate` のうち少なくとも 1 つは非 null (= DTO で空 PATCH を 400 として弾き済み)
     */
    fun update(
        id: Long,
        name: String?,
        birthDate: LocalDate?,
    ): Author {
        dsl
            .updateQuery(AUTHORS)
            .apply {
                name?.let { addValue(AUTHORS.NAME, it) }
                birthDate?.let { addValue(AUTHORS.BIRTH_DATE, it) }
                addConditions(AUTHORS.ID.eq(id))
            }.execute()
        return checkNotNull(findById(id)) { "author $id disappeared during update" }
    }
}
