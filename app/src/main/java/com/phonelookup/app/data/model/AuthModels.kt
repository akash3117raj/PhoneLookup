package com.phonelookup.app.data.model

import com.google.gson.annotations.SerializedName

/** Login request body */
data class LoginRequest(
    @SerializedName("license_key")
    val licenseKey: String,
    
    @SerializedName("username")
    val username: String? = null,

    @SerializedName("password")
    val password: String? = null
)

/** Professional API Response for Login */
data class LoginResponse(
    @SerializedName("status")
    val status: String = "failed",

    @SerializedName("token")
    val token: String? = null,

    @SerializedName("message")
    val message: String = ""
)
