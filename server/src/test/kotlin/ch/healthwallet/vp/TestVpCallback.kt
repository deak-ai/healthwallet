package ch.healthwallet.vp

import ch.healthwallet.repo.VerifierStatusCallback
import ch.healthwallet.repo.extractPrescription
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import java.io.File
import kotlin.test.assertNotNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TestVpCallback {


    @BeforeAll
    fun setup() {

    }



    @Test
    @Order(1)
    fun `test VP callback parsing`() {
        val fileContent = readJsonFile("src/test/resources/vp_status_callback.json")
        val vsc = json.decodeFromString<VerifierStatusCallback>(fileContent)
        println(vsc)
    }

    private val json = Json { ignoreUnknownKeys = true }

    fun readJsonFile(filePath: String): String {
        return File(filePath).readText()
    }

    @Test
    @Order(2)
    fun `test VP prescription extraction`() {
        val fileContent = readJsonFile("src/test/resources/vp_status_callback.json")
        val vsc = json.decodeFromString<VerifierStatusCallback>(fileContent)
        val pd = extractPrescription(vsc)
        assertNotNull(pd)
        println(pd)
    }

}