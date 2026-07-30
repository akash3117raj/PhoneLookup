package com.phonelookup.app.native_bridge

/**
 * Kotlin bridge to the native C++ performance library.
 * All JNI functions are compiled with -O3 for maximum speed.
 */
object NativeBridge {

    init {
        System.loadLibrary("phonelookup")
    }

    /** Validate phone number format natively — faster than Kotlin regex */
    external fun validatePhoneNumber(phoneNumber: String): Boolean

    /** Format phone result into rich display string using native string processing */
    external fun formatPhoneResult(
        number: String,
        name: String,
        carrier: String,
        location: String,
        lineType: String,
        countryCode: String
    ): String

    /** Strip non-digit characters from phone number natively */
    external fun cleanPhoneNumber(phoneNumber: String): String

    /** FNV-1a hash for ultra-fast cache key generation */
    external fun fastHash(input: String): Long

    /** High-resolution native timestamp for performance profiling */
    external fun nativeTimestampMs(): Double
}
