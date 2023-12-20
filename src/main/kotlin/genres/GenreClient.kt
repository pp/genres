package genres

import io.micronaut.http.annotation.Get
import io.micronaut.http.client.annotation.Client

@Client("https://binaryjazz.us")
interface GenreClient {

    @Get("/wp-json/genrenator/v1/story")
    fun fetchStory(): String
}
