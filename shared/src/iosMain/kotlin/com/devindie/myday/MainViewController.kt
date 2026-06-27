package com.devindie.myday

import androidx.compose.ui.window.ComposeUIViewController
import com.devindie.myday.billing.configureBillingPlatform

fun MainViewController() =
    ComposeUIViewController {
        configureBillingPlatform()
        doInitKoin()
        App()
    }
