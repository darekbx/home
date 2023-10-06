package com.darekbx.infopigula.repository.remote.model

import com.google.gson.annotations.SerializedName

data class CurrentUserResponse(
    val data: UserData,
    val message: String
)

data class UserData(
    @SerializedName("uid")
    val userId: String,
    @SerializedName("user_email")
    val userEmail: String,
    @SerializedName("dark_mode")
    val darkMode: String,
    @SerializedName("display_mode")
    val displayMode: String,
    @SerializedName("justified_text")
    val justifiedText: String,
    @SerializedName("send_releases_by_email")
    val sendReleasesByEmail: String,
    @SerializedName("slider_background")
    val sliderBackground: String?, // Nullable field
    val font: String,
    @SerializedName("font_size")
    val fontSize: String,
    @SerializedName("subscription_plan_id")
    val subscriptionPlanId: String,
    @SerializedName("subscription_plan_name")
    val subscriptionPlanName: String,
    @SerializedName("subscription_end")
    val subscriptionEnd: String,
    @SerializedName("subscription_renewal_period")
    val subscriptionRenewalPeriod: String
)

data class UserResponse(
    val uid: List<ValueItem>,
    val uuid: List<ValueItem>,
    val langcode: List<ValueItem>,
    @SerializedName("preferred_langcode")
    val preferredLangcode: List<ValueItem>,
    @SerializedName("preferred_admin_langcode")
    val preferredAdminLangcode: List<String>,
    val name: List<ValueItem>,
    val mail: List<ValueItem>,
    val timezone: List<ValueItem>,
    val created: List<ValueItem>,
    val changed: List<ValueItem>,
    @SerializedName("default_langcode")
    val defaultLangcode: List<ValueItem>,
    val metatag: Metatag,
    val path: List<PathItem>,
    @SerializedName("field_campaign_affiliate")
    val fieldCampaignAffiliate: List<String>,
    @SerializedName("field_dark_mode")
    val fieldDarkMode: List<ValueItem>,
    @SerializedName("field_display_mode")
    val fieldDisplayMode: List<ValueItem>,
    @SerializedName("field_font")
    val fieldFont: List<ValueItem>,
    @SerializedName("field_font_size")
    val fieldFontSize: List<ValueItem>,
    @SerializedName("field_justified_text")
    val fieldJustifiedText: List<ValueItem>,
    @SerializedName("field_last_password_reset")
    val fieldLastPasswordReset: List<ValueItem>,
    @SerializedName("field_password_expiration")
    val fieldPasswordExpiration: List<ValueItem>,
    @SerializedName("field_pending_expire_sent")
    val fieldPendingExpireSent: List<ValueItem>,
    @SerializedName("field_send_releases_by_email")
    val fieldSendReleasesByEmail: List<ValueItem>,
    @SerializedName("field_slider_background")
    val fieldSliderBackground: List<String>,
    @SerializedName("field_user_affiliate")
    val fieldUserAffiliate: List<String>,
    @SerializedName("user_picture")
    val userPicture: List<String>
)

data class ValueItem(
    val value: Any // Use a more specific type if possible
)

data class Metatag(
    val value: MetatagValue
)

data class MetatagValue(
    @SerializedName("canonical_url")
    val canonicalUrl: String,
    val title: String,
    val description: String
)

data class PathItem(
    val alias: String?,
    val pid: String?,
    val langcode: String
)

