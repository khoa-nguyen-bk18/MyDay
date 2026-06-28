package com.devindie.myday.architecture.layer

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import com.lemonappdev.konsist.api.ext.list.imports
import com.lemonappdev.konsist.api.verify.assertFalse
import org.junit.jupiter.api.Test

/** Konsist: production layers obey Clean Architecture dependency graph (domain ← data ← UI). */
class LayerArchitectureTest {

    @Test
    fun `layers follow clean architecture dependency rules`() {
        Konsist.scopeFromProduction()
            .assertArchitecture {
                val domain = Layer("Domain", "com.devindie.myday.domain..")
                val data = Layer("Data", "com.devindie.myday.data..")
                val presentation = Layer("Presentation", "com.devindie.myday.feature..")

                domain.doesNotDependOn(data)
                domain.doesNotDependOn(presentation)
                data.dependsOn(domain)
                presentation.dependsOn(domain)
                presentation.doesNotDependOn(data)
            }
    }

    @Test
    fun `shared di does not import data`() {
        Konsist.scopeFromPackage("com.devindie.myday.core.di..")
            .files
            .assertFalse { file ->
                file.imports.any { import ->
                    import.name.startsWith("com.devindie.myday.data.")
                }
            }
    }

    @Test
    fun `presentation does not depend on data packages`() {
        val presentationPackages =
            listOf(
                "com.devindie.myday.feature..",
                "com.devindie.myday.core.ui..",
                "com.devindie.myday.core.navigation..",
                "com.devindie.myday.core.constants..",
                "com.devindie.myday.core.di..",
            )
        val dataImportPrefix = "com.devindie.myday.data."

        presentationPackages.forEach { packageName ->
            Konsist.scopeFromPackage(packageName)
                .files
                .assertFalse { file ->
                    file.imports.any { import ->
                        import.name.startsWith(dataImportPrefix)
                    }
                }
        }
    }

    @Test
    fun `core navigation ui and constants do not import feature packages`() {
        val featureImportPrefix = "com.devindie.myday.feature."
        val productionCoreFiles =
            Konsist.scopeFromProduction()
                .files
                .filter { file ->
                    val packageName = file.packagee?.name ?: return@filter false
                    packageName.startsWith("com.devindie.myday.core.navigation") ||
                        packageName.startsWith("com.devindie.myday.core.ui") ||
                        packageName.startsWith("com.devindie.myday.core.constants")
                }

        productionCoreFiles.assertFalse { file ->
            file.imports.any { import ->
                import.name.startsWith(featureImportPrefix)
            }
        }
    }
}
