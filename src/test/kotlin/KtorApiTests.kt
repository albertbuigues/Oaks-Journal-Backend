import com.ortola.buigues.client.fetchKantoPokemon
import com.ortola.buigues.database.PokemonTable
import com.ortola.buigues.dto.PokemonDto
import com.ortola.buigues.initDatabase
import com.ortola.buigues.saveToLocalDatabase
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.mockk.every
import io.mockk.just
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(JUnit4::class)
class KtorApiTests {

    private val mockEngine = MockEngine {
        respond(
            content = """
                {
                    "id": 1,
                    "name": "bulbasaur",
                    "height": 7,
                    "weight": 69,
                    "cries": { "latest": "cry.ogg" },
                    "sprites": { "other": { "official-artwork": { "front_default": "img.png" } } },
                    "types": [{ "slot": 2, "type": { "name": "grass" } }, { "slot": 1, "type": { "name": "poison" } }]
                }
            """.trimIndent(),
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, "application/json")
        )
    }

    private val mockClient = HttpClient(mockEngine) {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    }

    @Before
    fun beforeEach() {
        initDatabase()
    }

    @After
    fun afterEach() {
        unmockkAll()
        transaction {
            SchemaUtils.drop(PokemonTable)
        }
    }

    @Test
    fun `When fetchKantoPokemon then data has 151 elements and saveToLocalDatabase is called once`() = runTest {
        mockkStatic(::saveToLocalDatabase)
        every { saveToLocalDatabase(any()) } just runs

        fetchKantoPokemon(mockClient)

        verify(exactly = 1) { saveToLocalDatabase(any()) }
        verify {
            saveToLocalDatabase(withArg { list ->
                assertEquals(151, list.size)
                assertEquals("Bulbasaur", list.first().name)
                assertEquals(0.7, list.first().heightInMeters)
                assertEquals(6.9, list.first().weightInKilos)
                assertEquals(listOf("poison","grass"), list.first().type)
            })
        }
    }

    @Test
    fun `when saveToLocalDatabaseCalledThenTableHasFieldsInside`() {
        transaction {
            PokemonTable.deleteAll()
        }

        val mockedList = listOf(
            PokemonDto(
                id = 1,
                name = "Charmander",
                heightInMeters = 0.5,
                weightInKilos = 5.0,
                cryLink = "char.ogg",
                type = listOf("fire"),
                spriteLink = "char.png"
            ),
            PokemonDto(
                id = 2,
                name = "Squirtle",
                heightInMeters = 0.6,
                weightInKilos = 3.0,
                cryLink = "sq.ogg",
                type = listOf("water"),
                spriteLink = "sq.png"
            )
        )

        saveToLocalDatabase(mockedList)

        val tableCount = transaction {
            PokemonTable.selectAll().count()
        }

        assertEquals(2, tableCount)
    }
}