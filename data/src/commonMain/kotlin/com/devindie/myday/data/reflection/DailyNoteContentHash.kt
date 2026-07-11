package com.devindie.myday.data.reflection

private const val HASH_PRIME = 31
private const val BYTE_MASK = 0xff
private const val HEX_RADIX = 16

internal fun dailyNoteContentHash(body: String): String = body
    .encodeToByteArray()
    .fold(0L) { acc, b -> (acc * HASH_PRIME) + (b.toLong() and BYTE_MASK.toLong()) }
    .toString(HEX_RADIX)
