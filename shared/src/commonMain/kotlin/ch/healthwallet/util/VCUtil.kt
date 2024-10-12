package ch.healthwallet.util

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive



fun extractMedicamentId(payload: JsonObject): String? {
    return payload["vc"]?.jsonObject
        ?.get("credentialSubject")?.jsonObject
        ?.get("prescription")?.jsonObject
        ?.get("Medicaments")?.jsonArray
        ?.get(0)?.jsonObject
        ?.get("Id")?.jsonPrimitive
        ?.content
}

fun isPrescription(payload: JsonObject): Boolean {
    return payload["vc"]?.jsonObject
        ?.get("credentialSubject")?.jsonObject
        ?.get("prescription") != null
}
