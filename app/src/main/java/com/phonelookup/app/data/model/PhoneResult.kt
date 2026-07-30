package com.phonelookup.app.data.model

import com.google.gson.annotations.SerializedName

/** Phone lookup result from API */
data class PhoneResult(
    @SerializedName("number")
    val number: String = "",

    @SerializedName("name")
    val name: String = "",

    @SerializedName("carrier")
    val carrier: String = "",

    @SerializedName("location")
    val location: String = "",

    @SerializedName("line_type")
    val lineType: String = "",

    @SerializedName("country_code")
    val countryCode: String = "",

    @SerializedName("valid")
    val valid: Boolean = true,

    @SerializedName("status")
    val status: String = "success"
)
