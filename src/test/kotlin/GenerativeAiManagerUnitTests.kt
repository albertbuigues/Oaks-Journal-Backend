import com.ortola.buigues.ai.GenerativeAiManager
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GenerativeAiManagerUnitTests {

    @Test
    fun `when correct question sent then correct answer`() {
        val question = "Which are the evolutions of Squirtle?"
        val expectedAnswers = listOf("wartortle", "blastoise")

        val answer = GenerativeAiManager.sendQuestionAndReceiveResponse(question)?.lowercase()

        assertNotNull(answer)
        assertTrue(expectedAnswers.all { answer.contains(it) })
    }
}