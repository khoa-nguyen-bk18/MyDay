package com.devindie.myday

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.ReportDrawn
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.devindie.myday.storage.DocumentTreePickerRegistry
import kotlinx.coroutines.CompletableDeferred

class MainActivity : ComponentActivity() {
    private var pendingTreePick: CompletableDeferred<Uri?>? = null

    private val openDocumentTreeLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            pendingTreePick?.complete(uri)
            pendingTreePick = null
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        DocumentTreePickerRegistry.register {
            val deferred = CompletableDeferred<Uri?>()
            pendingTreePick = deferred
            openDocumentTreeLauncher.launch(null)
            deferred.await()
        }

        setContent {
            ReportDrawn()
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
