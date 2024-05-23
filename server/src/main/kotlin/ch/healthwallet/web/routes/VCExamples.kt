package ch.healthwallet.web.routes

object VCExamples {

    val vcPrescriptionIssueRequest = """
        13dc576f-e7a6-4abd-a2c5-81e3d49d8487
    """.trimIndent()

    val vcPrescriptionIssueResponse = """
        openid-credential-offer://localhost/?credential_offer=%7B%22credential_issuer%22%3A%22http%3A%2F%2Flocalhost%3A8000%22%2C%22credentials%22%3A%5B%22VerifiableId%22%5D%2C%22grants%22%3A%7B%22authorization_code%22%3A%7B%22issuer_state%22%3A%22501414a4-c461-43f0-84b2-c628730c7c02%22%7D%7D%7D
    """.trimIndent()
}