package genres

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn

@Controller("/hello")
class HelloController(
    private val genreClient: GenreClient,
    private val genreProducer: GenreProducer
) { // Inject BeanContext

    @Get
    @Produces(MediaType.TEXT_PLAIN)
    @ExecuteOn(TaskExecutors.BLOCKING)
    fun index(): String {
        val type = "test"

        val story = try {
            genreClient.fetchStory()
        } catch (e: Exception) {
            return "Failed to fetch story: ${e.message}"
        }

        genreProducer.sendGenre(type, story)

        return "Fetched story: $story"
    }
}
