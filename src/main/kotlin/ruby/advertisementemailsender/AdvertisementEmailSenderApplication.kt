package ruby.advertisementemailsender

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AdvertisementEmailSenderApplication

fun main(args: Array<String>) {
    runApplication<AdvertisementEmailSenderApplication>(*args)
}
