package ruby.advertisementemailsender

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class AdvertisementEmailSenderApplication

fun main(args: Array<String>) {
    runApplication<AdvertisementEmailSenderApplication>(*args)
}
