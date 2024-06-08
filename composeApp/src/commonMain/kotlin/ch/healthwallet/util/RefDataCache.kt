package ch.healthwallet.util

import ch.healthwallet.data.chmed16a.MedicamentRefDataDTO
import ch.healthwallet.repo.PisServerRepository

class RefDataCache(private val pisServerRepo: PisServerRepository) {

    private val cache = mutableMapOf<String, MedicamentRefDataDTO>()

    suspend fun get(gtin: String): MedicamentRefDataDTO {
        return cache.getOrPut(gtin) {
            pisServerRepo.findMedicamentRefDataByGTIN(gtin).getOrThrow()
        }
    }
}