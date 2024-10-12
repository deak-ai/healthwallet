import ch.healthwallet.util.decodeJwtPayload
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.fail


class TestJwtUtil {

    @Test
    fun `Should throw IllegalArgumentException when JWT has more than three parts`() {
        val invalidJwt = "header.payload.signature.extra" // JWT with more than three parts
        try {
            decodeJwtPayload(invalidJwt)
            fail("Expected IllegalArgumentException to be thrown")
        } catch (e: IllegalArgumentException) {
            assertEquals("Invalid JWT format", e.message)
        }
    }

    @Test
    fun `Should throw IllegalArgumentException when JWT has less than three parts`() {
        val invalidJwt = "header.payload" // JWT with less than three parts
        try {
            decodeJwtPayload(invalidJwt)
            fail("Expected IllegalArgumentException to be thrown")
        } catch (e: IllegalArgumentException) {
            assertEquals("Invalid JWT format", e.message)
        }
    }

    @Test
    fun `Should correctly decode and parse a valid JWT payload into a JsonObject`() {
        val validJwt =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
        val expectedPayload = JsonObject(
            mapOf(
                "sub" to Json.parseToJsonElement("\"1234567890\""),
                "name" to Json.parseToJsonElement("\"John Doe\""),
                "iat" to Json.parseToJsonElement("1516239022")
            )
        )

        val result = decodeJwtPayload(validJwt)

        assertEquals(expectedPayload, result)
    }

    @Test
    fun `Should handle base64Url decoding errors gracefully`() {
        val invalidBase64Jwt =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalidPayload.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
        try {
            decodeJwtPayload(invalidBase64Jwt)
            fail("Expected IllegalArgumentException to be thrown due to base64Url decoding error")
        } catch (e: IllegalArgumentException) {
            assertContains(e.message ?: "", "Unexpected JSON token")
        } catch (e: Exception) {
            assertContains(e.message ?: "", "Failed to decode base64Url")
        }
    }

    @Test
    fun `Should throw an exception when the payload part is not valid base64Url`() {
        val invalidBase64Jwt =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalidPayload.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
        try {
            decodeJwtPayload(invalidBase64Jwt)
            fail("Expected IllegalArgumentException to be thrown due to base64Url decoding error")
        } catch (e: IllegalArgumentException) {
            assertContains(e.message ?: "", "Unexpected JSON token")
        } catch (e: Exception) {
            assertContains(e.message ?: "", "Failed to decode base64Url")
        }
    }

    @Test
    fun `Should throw an exception when the decoded payload is not valid JSON`() {
        val invalidJsonJwt =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.aW52YWxpZFBheWxvYWQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c" // "invalidPayload" in base64Url
        try {
            decodeJwtPayload(invalidJsonJwt)
            fail("Expected IllegalArgumentException to be thrown due to invalid JSON in payload")
        } catch (e: IllegalArgumentException) {
            assertContains(e.message ?: "", "Unexpected JSON token")
        } catch (e: Exception) {
            assertContains(e.message ?: "", "Failed to parse JSON")
        }
    }


    @Test
    fun `Should correctly decode a JWT with special characters in the payload`() {
        val jwtWithSpecialChars =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lICZhbXA7IFNwZWNpYWwgQ2hhcmFjdGVycyIsImlhdCI6MTUxNjIzOTAyMn0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
        val expectedPayload = JsonObject(
            mapOf(
                "sub" to Json.parseToJsonElement("\"1234567890\""),
                "name" to Json.parseToJsonElement("\"John Doe &amp; Special Characters\""),
                "iat" to Json.parseToJsonElement("1516239022")
            )
        )

        val result = decodeJwtPayload(jwtWithSpecialChars)

        assertEquals(expectedPayload, result)
    }

    @Test
    fun `Should correctly decode a JWT with numeric values in the payload`() {
        val jwtWithNumericValues =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTIzLCJ2YWx1ZSI6NDU2Ljc4OSwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
        val expectedPayload = JsonObject(
            mapOf(
                "id" to Json.parseToJsonElement("123"),
                "value" to Json.parseToJsonElement("456.789"),
                "iat" to Json.parseToJsonElement("1516239022")
            )
        )

        val result = decodeJwtPayload(jwtWithNumericValues)

        assertEquals(expectedPayload, result)
    }

    @Test
    fun `Should correctly decode a JWT with nested JSON objects in the payload`() {
        val jwtWithNestedJson =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImlkIjoiMTIzNDU2IiwibmFtZSI6IkpvaG4gRG9lIn0sImlhdCI6MTUxNjIzOTAyMn0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
        val expectedPayload = JsonObject(
            mapOf(
                "user" to JsonObject(
                    mapOf(
                        "id" to Json.parseToJsonElement("\"123456\""),
                        "name" to Json.parseToJsonElement("\"John Doe\"")
                    )
                ),
                "iat" to Json.parseToJsonElement("1516239022")
            )
        )

        val result = decodeJwtPayload(jwtWithNestedJson)

        assertEquals(expectedPayload, result)
    }
}