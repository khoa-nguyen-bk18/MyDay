package com.devindie.myday.fake

import com.devindie.myday.domain.model.reflection.VaultLink
import com.devindie.myday.domain.repository.VaultPickerPort

class FakeVaultPickerPort(var pickResult: Result<VaultLink> = Result.success(VaultLink("picked-token"))) :
    VaultPickerPort {
    var pickCount = 0

    override suspend fun pickVault(): Result<VaultLink> {
        pickCount++
        return pickResult
    }
}
