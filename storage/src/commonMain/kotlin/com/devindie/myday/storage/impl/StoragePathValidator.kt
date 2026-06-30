package com.devindie.myday.storage.impl

import com.devindie.myday.storage.api.StorageError

internal object StoragePathValidator {
    fun validate(relativePath: String): StorageError.InvalidPath? {
        if (relativePath.isEmpty()) {
            return null
        }
        if (relativePath.startsWith("/")) {
            return StorageError.InvalidPath(relativePath)
        }
        for (segment in relativePath.split('/')) {
            if (segment.isEmpty() || segment == "..") {
                return StorageError.InvalidPath(relativePath)
            }
        }
        return null
    }
}
