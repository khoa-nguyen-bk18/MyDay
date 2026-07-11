package com.devindie.myday.data.reflection

internal fun dailyNoteContentHash(body: String): String =
    body.encodeToByteArray().fold(0L) { acc, b -> (acc * 31) + (b.toLong() and 0xff) }.toString(16)
