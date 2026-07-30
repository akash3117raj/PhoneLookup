package com.phonelookup.app.data.model

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("owner") val owner: String,
    @SerializedName("result") val result: ApiResult,
    @SerializedName("cached") val cached: Boolean
)

data class ApiResult(
    @SerializedName("success") val success: Boolean,
    @SerializedName("count") val count: Int,
    @SerializedName("results") val results: List<ContactInfo>
)

data class ContactInfo(
    @SerializedName("NAME") val name: String?,
    @SerializedName("fname") val fatherName: String?,
    @SerializedName("ADDRESS") val address: String?,
    @SerializedName("circle") val circle: String?,
    @SerializedName("MOBILE") val mobile: String?,
    @SerializedName("alt") val alt: String?,
    @SerializedName("email") val email: String?
)

// --- Aadhar / Family Lookup Models ---

data class FamilyLookupResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("result") val result: FamilyResult
)

data class FamilyResult(
    @SerializedName("results") val results: List<RationCardInfo>
)

data class RationCardInfo(
    @SerializedName("ration_card_details") val details: RationCardDetails,
    @SerializedName("members") val members: List<FamilyMember>
)

data class RationCardDetails(
    @SerializedName("state_name") val stateName: String?,
    @SerializedName("district_name") val districtName: String?,
    @SerializedName("ration_card_no") val rationCardNo: String?,
    @SerializedName("scheme_name") val schemeName: String?
)

data class FamilyMember(
    @SerializedName("member_id") val memberId: String?,
    @SerializedName("member_name") val memberName: String?
)

// --- App Update Models ---

data class UpdateConfig(
    @SerializedName("latest_version_code") val latestVersionCode: Int,
    @SerializedName("latest_version_name") val latestVersionName: String,
    @SerializedName("update_url") val updateUrl: String,
    @SerializedName("update_notes") val updateNotes: String,
    @SerializedName("is_force_update") val isForceUpdate: Boolean,
    @SerializedName("maintenance_mode") val isMaintenanceMode: Boolean = false,
    @SerializedName("is_app_enabled") val isAppEnabled: Boolean = true,
    @SerializedName("maintenance_message") val maintenanceMessage: String = "System is under maintenance. Please try again later."
)
