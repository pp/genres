package genres

import io.micronaut.configuration.kafka.annotation.KafkaClient
import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.Topic

@KafkaClient
interface GenreProducer {

    @Topic("quickstart")
    fun sendGenre(@KafkaKey type: String, name: String)
}
