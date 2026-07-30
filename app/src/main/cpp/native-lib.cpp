#include <jni.h>
#include <string>
#include <sstream>
#include <cstring>
#include <chrono>
#include <android/log.h>

#define LOG_TAG "PhoneLookupNDK"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" {

/**
 * Validate phone number format using native C++ — faster than JVM regex.
 * Accepts 10-15 digit numbers with optional '+' prefix.
 */
JNIEXPORT jboolean JNICALL
Java_com_phonelookup_app_native_1bridge_NativeBridge_validatePhoneNumber(
        JNIEnv *env, jobject, jstring phoneNumber) {
    const char *phone = env->GetStringUTFChars(phoneNumber, nullptr);
    if (!phone) return JNI_FALSE;

    std::string cleaned;
    cleaned.reserve(16);
    bool hasPlus = false;

    for (const char *p = phone; *p; ++p) {
        if (*p == '+' && !hasPlus && cleaned.empty()) {
            cleaned += '+';
            hasPlus = true;
        } else if (*p >= '0' && *p <= '9') {
            cleaned += *p;
        }
    }

    env->ReleaseStringUTFChars(phoneNumber, phone);

    size_t startIdx = hasPlus ? 1 : 0;
    size_t digitCount = cleaned.size() - startIdx;

    return (digitCount >= 10 && digitCount <= 15) ? JNI_TRUE : JNI_FALSE;
}

/**
 * Format phone lookup result into a rich display string — native string
 * processing is significantly faster than Kotlin StringBuilder for
 * large concatenation workloads.
 */
JNIEXPORT jstring JNICALL
Java_com_phonelookup_app_native_1bridge_NativeBridge_formatPhoneResult(
        JNIEnv *env, jobject,
        jstring number, jstring name, jstring carrier,
        jstring location, jstring lineType, jstring countryCode) {

    const char *numStr = env->GetStringUTFChars(number, nullptr);
    const char *nameStr = env->GetStringUTFChars(name, nullptr);
    const char *carrierStr = env->GetStringUTFChars(carrier, nullptr);
    const char *locationStr = env->GetStringUTFChars(location, nullptr);
    const char *lineTypeStr = env->GetStringUTFChars(lineType, nullptr);
    const char *countryStr = env->GetStringUTFChars(countryCode, nullptr);

    std::ostringstream out;
    out << "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n";
    out << "  📱  PHONE LOOKUP RESULT\n";
    out << "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n";

    if (numStr && strlen(numStr) > 0)
        out << "📞  Number:    " << numStr << "\n";
    if (nameStr && strlen(nameStr) > 0)
        out << "👤  Name:      " << nameStr << "\n";
    if (carrierStr && strlen(carrierStr) > 0)
        out << "📡  Carrier:   " << carrierStr << "\n";
    if (locationStr && strlen(locationStr) > 0)
        out << "📍  Location:  " << locationStr << "\n";
    if (lineTypeStr && strlen(lineTypeStr) > 0)
        out << "📞  Line Type: " << lineTypeStr << "\n";
    if (countryStr && strlen(countryStr) > 0)
        out << "🌍  Country:   " << countryStr << "\n";

    out << "\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n";
    out << "  🔍 Powered by PhoneLookup Pro\n";
    out << "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";

    env->ReleaseStringUTFChars(number, numStr);
    env->ReleaseStringUTFChars(name, nameStr);
    env->ReleaseStringUTFChars(carrier, carrierStr);
    env->ReleaseStringUTFChars(location, locationStr);
    env->ReleaseStringUTFChars(lineType, lineTypeStr);
    env->ReleaseStringUTFChars(countryCode, countryStr);

    return env->NewStringUTF(out.str().c_str());
}

/**
 * Clean and normalize a phone number string — strips all non-digit
 * characters except leading '+'.
 */
JNIEXPORT jstring JNICALL
Java_com_phonelookup_app_native_1bridge_NativeBridge_cleanPhoneNumber(
        JNIEnv *env, jobject, jstring phoneNumber) {
    const char *phone = env->GetStringUTFChars(phoneNumber, nullptr);
    if (!phone) return env->NewStringUTF("");

    std::string cleaned;
    cleaned.reserve(16);
    bool hasPlus = false;

    for (const char *p = phone; *p; ++p) {
        if (*p == '+' && !hasPlus && cleaned.empty()) {
            cleaned += '+';
            hasPlus = true;
        } else if (*p >= '0' && *p <= '9') {
            cleaned += *p;
        }
    }

    env->ReleaseStringUTFChars(phoneNumber, phone);
    return env->NewStringUTF(cleaned.c_str());
}

/**
 * FNV-1a hash for ultra-fast cache key generation.
 * Used for memory-efficient result caching on the Kotlin side.
 */
JNIEXPORT jlong JNICALL
Java_com_phonelookup_app_native_1bridge_NativeBridge_fastHash(
        JNIEnv *env, jobject, jstring input) {
    const char *str = env->GetStringUTFChars(input, nullptr);
    if (!str) return 0;

    uint64_t hash = 14695981039346656037ULL;
    for (const char *p = str; *p; ++p) {
        hash ^= static_cast<uint64_t>(static_cast<unsigned char>(*p));
        hash *= 1099511628211ULL;
    }

    env->ReleaseStringUTFChars(input, str);
    return static_cast<jlong>(hash);
}

/**
 * Return high-resolution timestamp for performance profiling.
 */
JNIEXPORT jdouble JNICALL
Java_com_phonelookup_app_native_1bridge_NativeBridge_nativeTimestampMs(
        JNIEnv *, jobject) {
    auto now = std::chrono::high_resolution_clock::now();
    auto us = std::chrono::duration_cast<std::chrono::microseconds>(
            now.time_since_epoch()).count();
    return static_cast<jdouble>(us) / 1000.0;
}

} // extern "C"
