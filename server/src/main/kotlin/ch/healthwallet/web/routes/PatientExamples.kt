package ch.healthwallet.web.routes

object PatientExamples {
    val createPatientWithoutInsuranceCardNumberRequest = """
        {
            "FName":"Josie",
            "LName":"Duran",
            "BDt":"1991-06-22",
            "Gender":2}
    """.trimIndent()

    val createPatientWithoutInsuranceCardNumberResponse = """
        {
            "FName":"Josie",
            "LName":"Duran",
            "BDt":"1991-06-22",
            "Gender":2,
            "Ids": [
                {
                    "Type": 2,
                    "Val": 27
                }
            ]
        }
    """.trimIndent()


    val createPatientWithInsuranceCardNumberRequest = """
        {
            "FName":"John",
            "LName":"Dubon",
            "BDt":"1981-12-06",
            "Gender":1,
            "Ids": [
                {
                    "Type": 1,
                    "Val": "GHVS123456"
                }
            ]
        }
    """.trimIndent()

    val createPatientWithInsuranceCardNumberResponse = """
        {
            "FName":"John",
            "LName":"Dubon",
            "BDt":"1981-12-06",
            "Gender":1,
            "Ids": [
                {
                    "Type": 1,
                    "Val": "GHVS123456"
                },
                {
                    "Type": 2,
                    "Val": 28
                }
            ]
        }
    """.trimIndent()

}
