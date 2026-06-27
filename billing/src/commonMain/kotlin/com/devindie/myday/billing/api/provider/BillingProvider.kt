package com.devindie.myday.billing.api.provider

import com.devindie.myday.billing.api.BillingCustomerInfo
import com.devindie.myday.billing.api.BillingOfferings
import com.devindie.myday.billing.api.BillingPurchase
import com.devindie.myday.billing.api.BillingResult
import kotlinx.coroutines.flow.Flow

interface BillingProvider {
    suspend fun initialize(): BillingResult<Unit>

    suspend fun getOfferings(): BillingResult<BillingOfferings>

    suspend fun purchase(packageId: String): BillingResult<BillingPurchase>

    suspend fun restorePurchases(): BillingResult<BillingCustomerInfo>

    fun observeCustomerInfo(): Flow<BillingCustomerInfo>

    suspend fun logIn(appUserId: String): BillingResult<BillingCustomerInfo>

    suspend fun logOut(): BillingResult<BillingCustomerInfo>
}
