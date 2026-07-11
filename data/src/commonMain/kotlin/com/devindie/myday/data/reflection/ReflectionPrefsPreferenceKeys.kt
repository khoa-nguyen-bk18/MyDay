package com.devindie.myday.data.reflection

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.devindie.myday.domain.model.reflection.ReflectionConstants
import com.devindie.myday.domain.model.reflection.ReflectionPrefs

private val CONSENT_ACCEPTED_KEY = booleanPreferencesKey("reflection_consent_accepted")
private val FEATURE_ENABLED_KEY = booleanPreferencesKey("reflection_feature_enabled")
private val WINDOW_START_MINUTE_KEY = intPreferencesKey("reflection_window_start_minute")
private val WINDOW_END_MINUTE_KEY = intPreferencesKey("reflection_window_end_minute")
private val REFLECTION_FOLDER_KEY = stringPreferencesKey("reflection_folder")
private val MODEL_OVERRIDE_KEY = stringPreferencesKey("reflection_model_override")
private val USED_FALLBACK_PATH_NOTICE_SHOWN_KEY = booleanPreferencesKey("reflection_used_fallback_path_notice_shown")

internal fun Preferences.toReflectionPrefs(): ReflectionPrefs = ReflectionPrefs(
    consentAccepted = this[CONSENT_ACCEPTED_KEY] ?: false,
    featureEnabled = this[FEATURE_ENABLED_KEY] ?: false,
    windowStartMinuteOfDay = this[WINDOW_START_MINUTE_KEY] ?: ReflectionConstants.DEFAULT_WINDOW_START_MINUTE,
    windowEndMinuteOfDay = this[WINDOW_END_MINUTE_KEY] ?: ReflectionConstants.DEFAULT_WINDOW_END_MINUTE,
    reflectionFolder = this[REFLECTION_FOLDER_KEY] ?: ReflectionConstants.DEFAULT_REFLECTION_FOLDER,
    modelOverride = this[MODEL_OVERRIDE_KEY],
    usedFallbackPathNoticeShown = this[USED_FALLBACK_PATH_NOTICE_SHOWN_KEY] ?: false,
)

internal fun MutablePreferences.writeReflectionPrefs(prefs: ReflectionPrefs) {
    this[CONSENT_ACCEPTED_KEY] = prefs.consentAccepted
    this[FEATURE_ENABLED_KEY] = prefs.featureEnabled
    this[WINDOW_START_MINUTE_KEY] = prefs.windowStartMinuteOfDay
    this[WINDOW_END_MINUTE_KEY] = prefs.windowEndMinuteOfDay
    this[REFLECTION_FOLDER_KEY] = prefs.reflectionFolder
    val modelOverride = prefs.modelOverride
    if (modelOverride == null) {
        remove(MODEL_OVERRIDE_KEY)
    } else {
        this[MODEL_OVERRIDE_KEY] = modelOverride
    }
    this[USED_FALLBACK_PATH_NOTICE_SHOWN_KEY] = prefs.usedFallbackPathNoticeShown
}
