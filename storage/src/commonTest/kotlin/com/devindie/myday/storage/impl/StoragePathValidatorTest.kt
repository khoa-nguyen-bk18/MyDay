package com.devindie.myday.storage.impl

import com.devindie.myday.storage.api.StorageError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StoragePathValidatorTest {
    @Test
    fun emptyPath_isValid() {
        assertNull(StoragePathValidator.validate(""))
    }

    @Test
    fun nestedPath_isValid() {
        assertNull(StoragePathValidator.validate("notes/readme.md"))
    }

    @Test
    fun parentSegment_isInvalid() {
        assertEquals(
            StorageError.InvalidPath("../secret"),
            StoragePathValidator.validate("../secret"),
        )
    }

    @Test
    fun leadingSlash_isInvalid() {
        assertEquals(
            StorageError.InvalidPath("/leading"),
            StoragePathValidator.validate("/leading"),
        )
    }

    @Test
    fun emptySegment_isInvalid() {
        assertEquals(
            StorageError.InvalidPath("a//b"),
            StoragePathValidator.validate("a//b"),
        )
    }
}
