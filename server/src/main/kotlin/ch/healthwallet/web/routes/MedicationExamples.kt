package ch.healthwallet.web.routes

object MedicationExamples {

    val createMedicationPrescriptionRequest = """
        {
            "Auth": "GLN543210",
            "Medicaments": [
                {
                    "Id": "7680332730610",
                    "IdType": 2
                }
            ]
        }
    """.trimIndent()

    val createMedicationPrescriptionResponse = """
        {
            "Id": "7ea2d353-2642-40da-ab87-23d6b39eb066",
            "Auth": "GLN543210",
            "Dt": "2024-05-21T19:57:24+02:00",
            "Medicaments": [
                {
                    "Id": "7680332730610",
                    "IdType": 2
                }
            ],
            "Patient": {
                "FName": "Mina",
                "LName": "Meier",
                "BDt": "1999-12-17",
                "Ids": [
                    {
                        "Type": 2,
                        "Val": "3"
                    }
                ]
            }
        }      
    """.trimIndent()

}