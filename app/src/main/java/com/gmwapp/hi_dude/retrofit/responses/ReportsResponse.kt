package com.gmwapp.hi_dude.retrofit.responses

data class ReportsResponse(
    val success: Boolean,
    val message: String,
    val data: List<ReportData>
)

data class ReportData(
    val user_name: String,
    val today_calls: Int,
    val today_earnings: Int
)
