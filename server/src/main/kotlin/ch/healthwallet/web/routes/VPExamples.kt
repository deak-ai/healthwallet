package ch.healthwallet.web.routes

object VPExamples {
    val minimumVPRequestWithStatusCallback = """
        {
            "authorizeBaseUrl": "openid4vp://authorize",
            "responseMode": "direct_post",
            "statusCallbackUri": "https://pis.healthwallet.li/vp/status",
            "presentationDefinition": {
                "request_credentials": [
                    {
                      "format": "jwt_vc_json",
                      "type": "SwissMedicalPrescription"
                    }
                ]
            }
        }
    """.trimIndent()

    val completeVPRequestStructure = """
        {
            "authorizeBaseUrl": "openid4vp://authorize",
            "responseMode": "direct_post",
            "successRedirectUri": null,
            "errorRedirectUri": null,
            "statusCallbackUri": "https://pis.healthwallet.li/vp/status",
            "statusCallbackApiKey": null,
            "stateId": null,
            "presentationDefinition": {
                "request_credentials": [
                    {
                      "format": "jwt_vc_json",
                      "type": "SwissMedicalPrescription"
                    }
                ]
            }
        }
    """.trimIndent()

    val openId4VpAuthorizeUrl = """
        openid4vp://authorize?response_type=vp_token&client_id=&response_mode=direct_post&state=QVvKz9NxPcFB&presentation_definition_uri=https%3A%2F%2Fverifier.portal.walt.id%2Fopenid4vc%2Fpd%2FQVvKz9NxPcFB&client_id_scheme=redirect_uri&response_uri=https%3A%2F%2Fverifier.portal.walt.id%2Fopenid4vc%2Fverify%2FQVvKz9NxPcFB
    """.trimIndent()


}