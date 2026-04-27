package com.katsu.booklibrary.exception

/**
 * 対象リソース (URL の `{id}` 部分) が存在しない場合 (HTTP 404)。
 */
class NotFoundException(
    message: String,
) : RuntimeException(message)

/**
 * リクエストが参照する著者 (authorId) に DB 上存在しないものが含まれていた場合 (HTTP 422)。
 */
class UnknownAuthorException(
    message: String,
) : RuntimeException(message)

/**
 * ドメインルール違反 (例: PUBLISHED → UNPUBLISHED への遷移) が起きた場合 (HTTP 409)。
 */
class BusinessRuleException(
    message: String,
) : RuntimeException(message)
