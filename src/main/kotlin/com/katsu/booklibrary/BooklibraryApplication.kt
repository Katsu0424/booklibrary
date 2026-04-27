package com.katsu.booklibrary

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.TimeZone

@SpringBootApplication
class BooklibraryApplication

fun main(args: Array<String>) {
    // birthDate <= CURRENT_DATE の境界を JST に揃える (DB の timezone 設定とペア)
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo"))
    runApplication<BooklibraryApplication>(*args)
}
