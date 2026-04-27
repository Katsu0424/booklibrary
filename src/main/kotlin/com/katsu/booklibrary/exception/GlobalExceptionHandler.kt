package com.katsu.booklibrary.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * ドメイン例外を `ProblemDetail` (HTTP ステータス + detail) に変換するアドバイス。
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(e: NotFoundException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message ?: "not found")
    }

    @ExceptionHandler(UnknownAuthorException::class)
    fun handleUnknownAuthor(e: UnknownAuthorException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.message ?: "unknown author")
    }

    @ExceptionHandler(BusinessRuleException::class)
    fun handleBusinessRule(e: BusinessRuleException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.message ?: "conflict")
    }

    /**
     * リクエスト body の deserialize 失敗を 400 に変換する。
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadable(e: HttpMessageNotReadableException): ProblemDetail {
        val illegalArgumentCause =
            generateSequence(e as Throwable) { it.cause }
                .firstOrNull { it is IllegalArgumentException }
        val detail = illegalArgumentCause?.message ?: e.message ?: "malformed request body"
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail)
    }

    /**
     * Jackson でラップされない裸の [IllegalArgumentException] を 400 に変換する。
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message ?: "illegal argument")
    }
}
