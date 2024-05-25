package ch.healthwallet.qr

import io.ktor.http.Url
import io.ktor.http.protocolWithAuthority
import io.ktor.util.toMap
import org.junit.jupiter.api.Test

class TestCredentialOffer {


    @Test
    fun `Credential offer string can be parsed as Url`() {
        val offer = "openid-credential-offer://issuer.healthwallet.li/" +
                "?credential_offer_uri=https%3A%2F%2Fissuer.healthwallet.li" +
                "%2Fopenid4vc%2FcredentialOffer%3Fid%3Da0b5bedc-dc10-4031-b0c3-01e1e5ec87cb"

        val url = Url(offer)
        val reqParams = url.parameters.toMap()
        println(url.protocol.name)
        println(url.host)
        println(reqParams)

    }
}