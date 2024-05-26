package ch.healthwallet.qr

import java.io.FileOutputStream
import kotlin.test.Test
import kotlin.test.assertNotNull


class TestQrCodeGenerator {


    private fun getCredentialOffer():String =
        "openid-credential-offer://issuer.healthwallet.li/?" +
                "credential_offer_uri=https%3A%2F%2Fissuer.healthwallet.li%2F" +
                "openid4vc%2FcredentialOffer%3Fid%3De9f47026-0469-4ecb-a9ee-0d1684dba902"


    @Test
    fun `test QR code generation of credential offer`() {
        val qrBytes = generateQRCode(getCredentialOffer())
        assertNotNull(qrBytes)
        //FileOutputStream("test-qr-code.png").use { it.write(qrBytes) }
    }
}