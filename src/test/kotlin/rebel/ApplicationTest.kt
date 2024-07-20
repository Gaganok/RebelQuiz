package rebel

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*
import rebel.plugins.*
import rebel.service.QuestionType

class ApplicationTest {

    @Test
    fun test() {
        print( QuestionType.TEXT.toString())
    }

//    @Test
//    fun testRoot() = testApplication {
//        client.get("/").apply {
//            assertEquals(HttpStatusCode.OK, status)
//            assertEquals("Hello World!", bodyAsText())
//        }
//    }
}
