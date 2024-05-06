
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals


class TestJsonSerialization {

    private val json = Json {
        encodeDefaults = true
    }

    @Test
    fun `Test defaults serialization`() {
        val l = LoginRequest(type="email", email ="user@email.com", password = "password")
        assertEquals("email", l.type)
        val jsonString = json.encodeToString(LoginRequest.serializer(),l)
        println(jsonString)
        assertContains(jsonString, "type")
    }

    @Serializable
    data class LoginRequest(
        val type: String = "email",
        val email: String,
        val password: String,
    )
}