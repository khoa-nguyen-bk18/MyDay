package com.devindie.myday.architecture.module

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.imports
import com.lemonappdev.konsist.api.verify.assertFalse
import org.junit.jupiter.api.Test

/** Konsist: `androidApp` may import `data.di` only, not feature UI or other `data` packages. */
class AndroidAppBoundaryTest {

    @Test
    fun `androidApp does not import feature packages`() {
        androidAppFiles().assertFalse { file ->
            file.imports.any { import ->
                import.name.startsWith("com.devindie.myday.feature.")
            }
        }
    }

    @Test
    fun `androidApp imports data only for DI wiring`() {
        androidAppFiles().assertFalse { file ->
            file.imports.any { import ->
                import.name.startsWith("com.devindie.myday.data.") &&
                    !import.name.startsWith("com.devindie.myday.data.di.")
            }
        }
    }

    @Test
    fun `androidApp does not import RepositoryImpl`() {
        androidAppFiles().assertFalse { file ->
            file.imports.any { it.name.contains("RepositoryImpl") }
        }
    }

    private fun androidAppFiles() = Konsist.scopeFromPackage("com.devindie.myday..")
        .files
        .filter { it.path.contains("androidApp") }
}
