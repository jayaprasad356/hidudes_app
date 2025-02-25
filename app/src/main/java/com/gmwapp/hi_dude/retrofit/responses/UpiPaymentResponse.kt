package com.gmwapp.hi_dude.retrofit.responses

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import java.lang.reflect.Type


data class UpiPaymentResponse(
    val status: Boolean,
    val msg: String,
    @JsonAdapter(UpiDataDeserializer::class) val data: List<UpiData>
)

data class UpiData(
    val order_id: Long,
    val payment_url: String,
    val upi_id_hash: String,
    val upi_intent: UpiIntent
)

data class UpiIntent(
    val bhim_link: String?,
    val phonepe_link: String?,
    val paytm_link: String?,
    val gpay_link: String?
)

class UpiDataDeserializer : JsonDeserializer<List<UpiData>> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): List<UpiData> {
        return if (json.isJsonArray) {
            context.deserialize(json, typeOfT) // Parse normally if it's an array
        } else {
            listOf(context.deserialize(json, UpiData::class.java)) // Wrap in a list if it's a single object
        }
    }
}

