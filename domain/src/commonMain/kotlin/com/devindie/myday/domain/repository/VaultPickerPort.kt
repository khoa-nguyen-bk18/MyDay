package com.devindie.myday.domain.repository

import com.devindie.myday.domain.model.reflection.VaultLink

interface VaultPickerPort {
    suspend fun pickVault(): Result<VaultLink>
}
