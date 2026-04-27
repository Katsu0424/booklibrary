package com.katsu.booklibrary.support

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.assertThrows

fun rejectsInvalidArgs(vararg cases: Pair<String, () -> Any?>): List<DynamicTest> {
    return cases.map { (case, build) ->
        dynamicTest("rejects: $case") {
            assertThrows<IllegalArgumentException> { build() }
        }
    }
}
