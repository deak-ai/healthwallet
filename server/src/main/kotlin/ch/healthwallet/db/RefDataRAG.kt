package ch.healthwallet.db;

import ch.healthwallet.data.chmed16a.MedicamentRefDataDTO
import com.aallam.openai.api.embedding.EmbeddingRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction
import ch.healthwallet.data.chmed16a.MedicamentResult
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

class RefDataRAG(private val apiKey: String) {

    private val openAI: OpenAI = OpenAI(token = apiKey)

    fun searchSimilarMedicaments(query: String, limit: Int = 5): List<MedicamentRefDataDTO> {
        val embedding = getEmbedding(query)
        return searchByEmbedding(embedding, limit)
    }

     fun getEmbedding(text: String): List<Double> = runBlocking {
        val request = EmbeddingRequest(
            model = ModelId("text-embedding-ada-002"),
            input = listOf(text)
        )
        openAI.embeddings(request).embeddings.first().embedding
    }

     fun countEmbeddings(): Int {
        return transaction {
            var count = 0
            exec("SELECT count(*) from medicamentsrefdata_embedding") { rs ->
                while (rs.next()) {
                    count = rs.getInt(1)
                }
            }
            count
        }
    }



    private fun searchByEmbedding(embedding: List<Double>, limit: Int): List<MedicamentRefDataDTO> {
        // Convert the embedding list into a PostgreSQL-compatible array string
        val embeddingArray = embedding.joinToString(
            separator = ",",
            prefix = "[",
            postfix = "]"
        )

        return transaction {
            val results = mutableListOf<MedicamentRefDataDTO>()

            // Execute the raw SQL query
            exec("""
            SELECT 
                m.id, 
                m.dt, 
                m.atype, 
                m.gtin, 
                m.swmc_authnr, 
                m.name_de, 
                m.name_fr, 
                m.atc, 
                m.auth_holder_name, 
                m.auth_holder_gln, 
                me.embedding <-> '$embeddingArray'::vector AS similarity_score
            FROM 
                public.medicamentsrefdata m
            JOIN 
                public.medicamentsrefdata_embedding me
            ON 
                m.id = me.id
            ORDER BY 
                similarity_score
            LIMIT 
                $limit;
        """) { rs ->
                while (rs.next()) {
                    // Extract results and map them to MedicamentResult
                    results.add(
//                        MedicamentResult(
//                            id = rs.getInt("id"),
//                            dt = rs.getTimestamp("dt").toLocalDateTime().format(ISO_LOCAL_DATE_TIME),
//                            atype = rs.getString("atype"),
//                            gtin = rs.getString("gtin"),
//                            swmcAuthnr = rs.getString("swmc_authnr"),
//                            nameDe = rs.getString("name_de"),
//                            nameFr = rs.getString("name_fr"),
//                            atc = rs.getString("atc"),
//                            authHolderName = rs.getString("auth_holder_name"),
//                            authHolderGln = rs.getString("auth_holder_gln"),
//                            similarityScore = rs.getDouble("similarity_score")
//                        )

                         MedicamentRefDataDTO(
                            atype = rs.getString("atype"),
                            gtin = rs.getString("gtin"),
                            swmcAuthnr = rs.getString("swmc_authnr"),
                            nameDe = rs.getString("name_de"),
                            nameFr = rs.getString("name_fr"),
                            atc = rs.getString("atc"),
                            authHolderName = rs.getString("auth_holder_name"),
                            authHolderGln = rs.getString("auth_holder_gln"),
                            date = rs.getTimestamp("dt").toLocalDateTime().format(ISO_LOCAL_DATE_TIME),
                            similarityScore = rs.getDouble("similarity_score")
                        )
                    )
                }
            }
            results
        }
    }
}


