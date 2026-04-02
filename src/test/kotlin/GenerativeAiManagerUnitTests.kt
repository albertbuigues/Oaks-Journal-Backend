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

    @Test
    fun `when send question about other thing then custom message`() {
        val question = "Main character of One Piece anime"
        val expectedAnswerContainsText = "sorry, I don't have any answer for that".lowercase()

        val answer = GenerativeAiManager.sendQuestionAndReceiveResponse(question)?.lowercase()

        assertNotNull(answer)
        assertTrue(answer.contains(expectedAnswerContainsText))
    }

    @Test
    fun `when send question about other region then custom message`() {
        val question = "Which is the evolution of Totodile"
        val expectedAnswerContainsText = "sorry, I don't have any answer for that".lowercase()

        val answer = GenerativeAiManager.sendQuestionAndReceiveResponse(question)?.lowercase()

        assertNotNull(answer)
        assertTrue(answer.contains(expectedAnswerContainsText))
    }
}