package ch.healthwallet.repo

import ch.healthwallet.data.chmed16a.MedicamentRefDataDTO
import kotlin.Result

interface  PisServerRepository {

    suspend fun findMedicamentRefDataBySubstring(substring: String): Result<List<MedicamentRefDataDTO>>

    suspend fun findMedicamentRefDataByGTIN(gtin: String): Result<MedicamentRefDataDTO>


}