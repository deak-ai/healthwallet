package ch.healthwallet.qr

import io.ktor.http.Url
import java.io.FileOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class TestQrCodeGenerator {


    companion object {
        val credentialOffer:String =
            "openid-credential-offer://issuer.healthwallet.li/?" +
                    "credential_offer_uri=https%3A%2F%2Fissuer.healthwallet.li%2F" +
                    "openid4vc%2FcredentialOffer%3Fid%3De9f47026-0469-4ecb-a9ee-0d1684dba902"

        val verifyUrl : String = "openid4vp://authorize?response_type=vp_token&" +
                "client_id=&response_mode=direct_post&state=tPECMRcJkVdO&presentation_definition_uri=" +
                "http%3A%2F%2Fverifier-api%3A7003%2Fopenid4vc%2Fpd%2FtPECMRcJkVdO&" +
                "client_id_scheme=redirect_uri&response_uri=" +
                "http%3A%2F%2Fverifier-api%3A7003%2Fopenid4vc%2Fverify%2FtPECMRcJkVdO"

    }

    @Test
    fun `test QR code generation of credential offer`() {
        val qrBytes = generateQRCode(credentialOffer)
        assertNotNull(qrBytes)
        //FileOutputStream("test-qr-code.png").use { it.write(qrBytes) }
    }

    @Test
    fun `test openid4vp qr code parsing`() {
        val url = Url(verifyUrl)
        assertEquals("openid4vp", url.protocol.name)
        assertEquals("authorize", url.host)

    }



}