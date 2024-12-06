package ruby.advertisementemailsender.event

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import ruby.advertisementemailsender.Advertisement

@Service
class AdvertisementEventService(
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun sendMail(advertisement: Advertisement) {
        val event = AdvertisementCreatedEvent(
            title = advertisement.title!!,
            cost = advertisement.cost!!
        )
        applicationEventPublisher.publishEvent(event)
    }
}
