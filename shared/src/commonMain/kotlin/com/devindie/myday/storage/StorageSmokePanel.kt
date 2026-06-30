package com.devindie.myday.storage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devindie.myday.storage.api.StorageAccessMode
import com.devindie.myday.storage.api.StorageClient
import com.devindie.myday.storage.api.StorageError
import com.devindie.myday.storage.api.StorageLocationToken
import com.devindie.myday.storage.api.StoragePickRequest
import com.devindie.myday.storage.api.StorageResult
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private const val SMOKE_FILE = "myday-storage-smoke.txt"
private const val SMOKE_TEXT = "myday storage smoke test"

@Composable
fun StorageSmokePanel(
    modifier: Modifier = Modifier,
    storage: StorageClient = koinInject(),
) {
    var status by remember { mutableStateOf("Pick a folder, then run the write/read smoke check.") }
    var token by remember { mutableStateOf<StorageLocationToken?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Storage smoke test",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = status,
            style = MaterialTheme.typography.bodyMedium,
        )
        Button(
            onClick = {
                scope.launch {
                    when (
                        val result =
                            storage.pickFolder(StoragePickRequest(StorageAccessMode.ReadWrite))
                    ) {
                        is StorageResult.Success -> {
                            token = result.value
                            status = "pick: OK (token saved for smoke test)"
                        }
                        is StorageResult.Cancelled -> status = "pick: cancelled"
                        is StorageResult.Failure -> status = "pick: ${result.error.format()}"
                    }
                }
            },
        ) {
            Text("Pick folder")
        }
        Button(
            onClick = {
                val picked = token
                if (picked == null) {
                    status = "write/read: pick a folder first"
                    return@Button
                }
                scope.launch {
                    status =
                        when (val write = storage.writeText(picked, SMOKE_FILE, SMOKE_TEXT)) {
                            is StorageResult.Success -> {
                                when (val read = storage.readText(picked, SMOKE_FILE)) {
                                    is StorageResult.Success ->
                                        if (read.value == SMOKE_TEXT) {
                                            "write/read: OK"
                                        } else {
                                            "write/read: content mismatch"
                                        }
                                    is StorageResult.Cancelled -> "write/read: cancelled"
                                    is StorageResult.Failure -> "read: ${read.error.format()}"
                                }
                            }
                            is StorageResult.Cancelled -> "write/read: cancelled"
                            is StorageResult.Failure -> "write: ${write.error.format()}"
                        }
                }
            },
        ) {
            Text("Write and read smoke file")
        }
        Button(
            onClick = {
                val picked = token
                if (picked == null) {
                    status = "delete: pick a folder first"
                    return@Button
                }
                scope.launch {
                    status =
                        when (val result = storage.delete(picked, SMOKE_FILE)) {
                            is StorageResult.Success -> "delete: OK"
                            is StorageResult.Cancelled -> "delete: cancelled"
                            is StorageResult.Failure -> "delete: ${result.error.format()}"
                        }
                }
            },
        ) {
            Text("Delete smoke file")
        }
    }
}

private fun StorageError.format(): String =
    when (this) {
        StorageError.NotConfigured -> "not configured (storage disabled or picker host missing)"
        StorageError.PermissionDenied -> "permission denied"
        StorageError.NotFound -> "not found"
        is StorageError.InvalidPath -> "invalid path: $relativePath"
        is StorageError.Io -> "io: $message"
    }
