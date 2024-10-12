package ch.healthwallet.util

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import saschpe.kase64.base64UrlDecoded


fun decodeJwtPayload(jwt: String): JsonObject {
    // Split the JWT into its three parts (header, payload, signature)
    val parts = jwt.split(".")
    if (parts.size != 3) throw IllegalArgumentException("Invalid JWT format")

    // The payload is the second part, decode it using Kase64
    val decodedPayload = parts[1].base64UrlDecoded

    // Parse the decoded payload (which is a JSON string) into a JsonObject
    return Json.decodeFromString<JsonObject>(decodedPayload)
}

