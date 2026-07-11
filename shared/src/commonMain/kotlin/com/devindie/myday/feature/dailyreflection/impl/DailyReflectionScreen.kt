package com.devindie.myday.feature.dailyreflection.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devindie.myday.core.ui.theme.AppTheme
import com.devindie.myday.domain.model.reflection.NotHelpfulReason
import com.devindie.myday.domain.model.reflection.ReflectionPrefs
import org.koin.compose.viewmodel.koinViewModel

private const val OPENROUTER_KEYS_URL = "https://openrouter.ai/keys"

@Composable
internal fun DailyReflectionScreen(
    modifier: Modifier = Modifier,
    viewModel: DailyReflectionViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    DailyReflectionContent(
        state = state,
        onRefresh = viewModel::refresh,
        onPickVault = viewModel::pickVault,
        onAcceptConsent = viewModel::acceptConsent,
        onDeclineConsent = viewModel::declineConsent,
        onSaveKey = viewModel::saveKey,
        onClearKey = viewModel::clearKey,
        onToggleEnabled = viewModel::toggleEnabled,
        onGenerate = viewModel::generate,
        onCancelGenerate = viewModel::cancelGenerate,
        onRegenerate = viewModel::regenerate,
        onShorten = viewModel::shorten,
        onEditMarkdown = viewModel::editMarkdown,
        onRequestSave = viewModel::requestSave,
        onConfirmSave = { viewModel.confirmSave(replace = true) },
        onCancelSave = viewModel::cancelSave,
        onDismissError = viewModel::dismissError,
        onFeedback = viewModel::submitHelpfulFeedback,
        onRunAutoDraftNow = viewModel::runAutoDraftNow,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DailyReflectionContent(
    state: DailyReflectionUiState,
    onRefresh: () -> Unit,
    onPickVault: () -> Unit,
    onAcceptConsent: () -> Unit,
    onDeclineConsent: () -> Unit,
    onSaveKey: (String) -> Unit,
    onClearKey: () -> Unit,
    onToggleEnabled: (Boolean) -> Unit,
    onGenerate: () -> Unit,
    onCancelGenerate: () -> Unit,
    onRegenerate: () -> Unit,
    onShorten: () -> Unit,
    onEditMarkdown: (String) -> Unit,
    onRequestSave: () -> Unit,
    onConfirmSave: () -> Unit,
    onCancelSave: () -> Unit,
    onDismissError: () -> Unit,
    onFeedback: (Boolean, NotHelpfulReason?) -> Unit,
    onRunAutoDraftNow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current

    if (state.saveConfirmRequired) {
        AlertDialog(
            onDismissRequest = onCancelSave,
            title = { Text("Replace existing reflection?") },
            text = {
                Text("A reflection file already exists for today. Replace it with this draft?")
            },
            confirmButton = {
                TextButton(onClick = onConfirmSave) {
                    Text("Replace")
                }
            },
            dismissButton = {
                TextButton(onClick = onCancelSave) {
                    Text("Cancel")
                }
            },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Reflect") },
                actions = {
                    TextButton(onClick = onRefresh) {
                        Text("Refresh")
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (val setup = state.setup) {
                SetupState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                SetupState.NeedsVault -> {
                    SetupScrollColumn {
                        SetupIntroCard(
                            title = "Link your vault",
                            body =
                            "Choose your Obsidian vault folder so MyDay can read " +
                                "today's daily note and save reflections.",
                        )
                        Button(
                            onClick = onPickVault,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Link vault")
                        }
                    }
                }

                SetupState.NeedsConsent -> {
                    SetupScrollColumn {
                        SetupIntroCard(
                            title = "Privacy notice",
                            body =
                            "Daily Reflection sends your daily note text to OpenRouter " +
                                "using your own API key. MyDay does not store your journal " +
                                "on our servers.",
                        )
                        ConsentRow(
                            onAccept = onAcceptConsent,
                            onDecline = onDeclineConsent,
                        )
                    }
                }

                SetupState.NeedsKey -> {
                    var keyInput by remember { mutableStateOf("") }
                    SetupScrollColumn {
                        SetupIntroCard(
                            title = "OpenRouter API key",
                            body =
                            "Create a key at OpenRouter and paste it here. Your key stays " +
                                "on this device in secure storage.",
                        )
                        TextButton(
                            onClick = { uriHandler.openUri(OPENROUTER_KEYS_URL) },
                        ) {
                            Text("Get a key at openrouter.ai/keys")
                        }
                        OutlinedTextField(
                            value = keyInput,
                            onValueChange = { keyInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("API key") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                        )
                        Button(
                            onClick = { onSaveKey(keyInput) },
                            enabled = keyInput.isNotBlank(),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Save key")
                        }
                    }
                }

                is SetupState.Ready -> {
                    ReadyReflectionBody(
                        state = state,
                        prefs = setup.prefs,
                        onToggleEnabled = onToggleEnabled,
                        onGenerate = onGenerate,
                        onRegenerate = onRegenerate,
                        onShorten = onShorten,
                        onEditMarkdown = onEditMarkdown,
                        onRequestSave = onRequestSave,
                        onClearKey = onClearKey,
                        onDismissError = onDismissError,
                        onFeedback = onFeedback,
                        onRunAutoDraftNow = onRunAutoDraftNow,
                    )
                }
            }

            if (state.isGenerating) {
                GeneratingOverlay(onCancel = onCancelGenerate)
            }
        }
    }
}

@Composable
private fun SetupScrollColumn(content: @Composable () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { content() }
    }
}

@Composable
private fun SetupIntroCard(title: String, body: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ConsentRow(onAccept: () -> Unit, onDecline: () -> Unit) {
    var checked by remember { mutableStateOf(false) }
    ListItem(
        headlineContent = { Text("I understand and want to continue") },
        leadingContent = {
            Checkbox(
                checked = checked,
                onCheckedChange = { checked = it },
            )
        },
    )
    Button(
        onClick = onAccept,
        enabled = checked,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Accept and continue")
    }
    OutlinedButton(
        onClick = onDecline,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Not now")
    }
}

@Composable
private fun ReadyReflectionBody(
    state: DailyReflectionUiState,
    prefs: ReflectionPrefs,
    onToggleEnabled: (Boolean) -> Unit,
    onGenerate: () -> Unit,
    onRegenerate: () -> Unit,
    onShorten: () -> Unit,
    onEditMarkdown: (String) -> Unit,
    onRequestSave: () -> Unit,
    onClearKey: () -> Unit,
    onDismissError: () -> Unit,
    onFeedback: (Boolean, NotHelpfulReason?) -> Unit,
    onRunAutoDraftNow: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            StatusCard(state = state)
        }
        item {
            ListItem(
                headlineContent = { Text("Auto-draft") },
                supportingContent = {
                    Text(
                        text =
                        "Window ${formatMinuteOfDay(prefs.windowStartMinuteOfDay)}–" +
                            formatMinuteOfDay(prefs.windowEndMinuteOfDay),
                    )
                },
                trailingContent = {
                    Switch(
                        checked = prefs.featureEnabled,
                        onCheckedChange = onToggleEnabled,
                    )
                },
            )
        }
        if (state.debugToolsEnabled) {
            item {
                OutlinedButton(
                    onClick = onRunAutoDraftNow,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Run Auto-Draft now (debug)")
                }
            }
        }
        item {
            if (state.draft == null) {
                Button(
                    onClick = onGenerate,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Text("Generate reflection", modifier = Modifier.padding(start = 8.dp))
                }
            } else {
                DraftReviewSection(
                    markdown = state.editableMarkdown,
                    feedbackSubmitted = state.feedbackSubmitted,
                    onEditMarkdown = onEditMarkdown,
                    onRegenerate = onRegenerate,
                    onShorten = onShorten,
                    onSave = onRequestSave,
                    onFeedback = onFeedback,
                )
            }
        }
        item {
            OutlinedButton(
                onClick = onClearKey,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Remove API key")
            }
        }
        state.errorMessage?.let { message ->
            item {
                ErrorBanner(message = message, onDismiss = onDismissError)
            }
        }
    }
}

@Composable
private fun StatusCard(state: DailyReflectionUiState) {
    val statusText =
        when {
            state.draft == null -> "No draft for today yet."
            state.sourceChangedSinceDraft ->
                "Draft ready — your daily note changed since this draft was generated."
            state.sourceTruncated ->
                "Draft ready — today's note was truncated before generation."
            else -> "Draft ready for review."
        }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = statusText, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun DraftReviewSection(
    markdown: String,
    feedbackSubmitted: Boolean,
    onEditMarkdown: (String) -> Unit,
    onRegenerate: () -> Unit,
    onShorten: () -> Unit,
    onSave: () -> Unit,
    onFeedback: (Boolean, NotHelpfulReason?) -> Unit,
) {
    var showReasons by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Draft", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = markdown,
            onValueChange = onEditMarkdown,
            modifier = Modifier.fillMaxWidth(),
            minLines = 12,
        )
        Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
            Text("Save to vault")
        }
        OutlinedButton(onClick = onRegenerate, modifier = Modifier.fillMaxWidth()) {
            Text("Regenerate")
        }
        OutlinedButton(onClick = onShorten, modifier = Modifier.fillMaxWidth()) {
            Text("Shorten")
        }
        if (!feedbackSubmitted) {
            Text(text = "Was this helpful?", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = false,
                    onClick = { onFeedback(true, null) },
                    label = { Text("Helpful") },
                )
                FilterChip(
                    selected = showReasons,
                    onClick = { showReasons = !showReasons },
                    label = { Text("Not helpful") },
                )
            }
            if (showReasons) {
                NotHelpfulReason.entries.forEach { reason ->
                    TextButton(
                        onClick = { onFeedback(false, reason) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(reason.name)
                    }
                }
            }
        } else {
            Text(
                text = "Thanks for the feedback.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ErrorBanner(message: String, onDismiss: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            TextButton(onClick = onDismiss) {
                Text("Dismiss")
            }
        }
    }
}

@Composable
private fun GeneratingOverlay(onCancel: () -> Unit) {
    Box(
        modifier =
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Card {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                CircularProgressIndicator()
                Text("Generating reflection…", style = MaterialTheme.typography.bodyLarge)
                OutlinedButton(onClick = onCancel) {
                    Text("Cancel")
                }
            }
        }
    }
}

private fun formatMinuteOfDay(minute: Int): String {
    val hours = minute / 60
    val minutes = minute % 60
    return "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}"
}

@Preview
@Composable
private fun DailyReflectionNeedsConsentPreview() {
    AppTheme {
        DailyReflectionContent(
            state = DailyReflectionUiState(setup = SetupState.NeedsConsent),
            onRefresh = {},
            onPickVault = {},
            onAcceptConsent = {},
            onDeclineConsent = {},
            onSaveKey = {},
            onClearKey = {},
            onToggleEnabled = {},
            onGenerate = {},
            onCancelGenerate = {},
            onRegenerate = {},
            onShorten = {},
            onEditMarkdown = {},
            onRequestSave = {},
            onConfirmSave = {},
            onCancelSave = {},
            onDismissError = {},
            onFeedback = { _, _ -> },
            onRunAutoDraftNow = {},
        )
    }
}

@Preview
@Composable
private fun DailyReflectionReadyPreview() {
    AppTheme {
        DailyReflectionContent(
            state =
            DailyReflectionUiState(
                setup = SetupState.Ready(ReflectionPrefs(consentAccepted = true, featureEnabled = true)),
                draft = null,
            ),
            onRefresh = {},
            onPickVault = {},
            onAcceptConsent = {},
            onDeclineConsent = {},
            onSaveKey = {},
            onClearKey = {},
            onToggleEnabled = {},
            onGenerate = {},
            onCancelGenerate = {},
            onRegenerate = {},
            onShorten = {},
            onEditMarkdown = {},
            onRequestSave = {},
            onConfirmSave = {},
            onCancelSave = {},
            onDismissError = {},
            onFeedback = { _, _ -> },
            onRunAutoDraftNow = {},
        )
    }
}
