package ch.healthwallet.db

import ch.healthwallet.refdata.Article
import ch.healthwallet.refdata.Item
import nl.adaptivity.xmlutil.serialization.XML
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class TestRefData {

    @Test
    fun `test load ref data from file`() {
        val fileContent = File("src/test/resources/refdata/Articles_3.xml").readText()
        //val fileContent = File("src/main/resources/refdata/Articles_ALL_ALL_20240429170557.xml")
        // .readText()
        val article: Article = XML.decodeFromString(Article.serializer(), fileContent)

        val medicamentList: List<Item> = article.items

        assertEquals(3, medicamentList.size)

    }

}