package com.katsu.booklibrary

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BooklibraryApplication

fun main(args: Array<String>) {
	runApplication<BooklibraryApplication>(*args)
}
