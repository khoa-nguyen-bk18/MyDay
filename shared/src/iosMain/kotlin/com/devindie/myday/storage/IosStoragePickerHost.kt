package com.devindie.myday.storage

import com.devindie.myday.storage.api.StorageError
import com.devindie.myday.storage.api.StorageLocationToken
import com.devindie.myday.storage.api.StoragePickRequest
import com.devindie.myday.storage.api.StorageResult
import com.devindie.myday.storage.api.provider.StoragePickerHost
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCSignatureOverride
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.NSURLBookmarkCreationWithSecurityScope
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerMode
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.darwin.NSObject
import platform.posix.memcpy
import kotlin.coroutines.resume
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class, ExperimentalEncodingApi::class)
class IosStoragePickerHost : StoragePickerHost {
    override suspend fun pickFolder(request: StoragePickRequest): StorageResult<StorageLocationToken> =
        suspendCancellableCoroutine { continuation ->
            val presenter =
                topViewController() ?: run {
                    continuation.resume(StorageResult.Failure(StorageError.NotConfigured))
                    return@suspendCancellableCoroutine
                }

            val picker =
                UIDocumentPickerViewController(
                    documentTypes = listOf("public.folder"),
                    inMode = UIDocumentPickerMode.UIDocumentPickerModeOpen,
                )

            var delegate: FolderPickerDelegate? = null
            delegate =
                FolderPickerDelegate(
                    onComplete = { result ->
                        delegate?.let { FolderPickerDelegateHolder.release(it) }
                        picker.dismissViewControllerAnimated(true, completion = null)
                        if (continuation.isActive) {
                            continuation.resume(result)
                        }
                    },
                )
            FolderPickerDelegateHolder.retain(delegate!!)
            picker.delegate = delegate
            presenter.presentViewController(picker, animated = true, completion = null)

            continuation.invokeOnCancellation {
                delegate?.let { FolderPickerDelegateHolder.release(it) }
                picker.dismissViewControllerAnimated(true, completion = null)
            }
        }

    private fun topViewController(): UIViewController? {
        val application = UIApplication.sharedApplication

        @Suppress("UNCHECKED_CAST")
        val windows = application.windows as? List<UIWindow>
        val window = windows?.firstOrNull() ?: application.keyWindow
        var controller = window?.rootViewController ?: return null
        while (controller.presentedViewController != null) {
            controller = controller.presentedViewController!!
        }
        return controller
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class, ExperimentalEncodingApi::class)
private class FolderPickerDelegate(private val onComplete: (StorageResult<StorageLocationToken>) -> Unit) :
    NSObject(),
    UIDocumentPickerDelegateProtocol {
    @ObjCSignatureOverride
    override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
        val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL
        if (url == null) {
            onComplete(StorageResult.Cancelled)
            return
        }
        val bookmarkData =
            url.bookmarkDataWithOptions(
                options = NSURLBookmarkCreationWithSecurityScope,
                includingResourceValuesForKeys = null,
                relativeToURL = null,
                error = null,
            )
        if (bookmarkData == null) {
            onComplete(StorageResult.Failure(StorageError.PermissionDenied))
            return
        }
        val tokenValue = Base64.encode(bookmarkData.toByteArray())
        onComplete(StorageResult.Success(StorageLocationToken(tokenValue)))
    }

    @ObjCSignatureOverride
    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
        onComplete(StorageResult.Cancelled)
    }
}

@OptIn(ExperimentalForeignApi::class)
private object FolderPickerDelegateHolder {
    private val activeDelegates = mutableListOf<FolderPickerDelegate>()

    fun retain(delegate: FolderPickerDelegate) {
        activeDelegates += delegate
    }

    fun release(delegate: FolderPickerDelegate) {
        activeDelegates -= delegate
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val length = this.length.toInt()
    if (length == 0) {
        return ByteArray(0)
    }
    return ByteArray(length).apply {
        usePinned { pinned ->
            memcpy(pinned.addressOf(0), bytes, this@toByteArray.length)
        }
    }
}
