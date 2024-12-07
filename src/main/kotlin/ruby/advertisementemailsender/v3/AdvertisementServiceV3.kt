package ruby.advertisementemailsender.v3

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ruby.advertisementemailsender.Advertisement
import ruby.advertisementemailsender.AdvertisementPost
import ruby.advertisementemailsender.AdvertisementRepository
import ruby.advertisementemailsender.v3.event.AdvertisementCreatedEvent

@Service
class AdvertisementServiceV3(
    private val advertisementRepository: AdvertisementRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    private val logger = org.slf4j.LoggerFactory.getLogger(javaClass)

    @Transactional
    fun add(advertisementPost: AdvertisementPost) {
        val advertisement = Advertisement(
            title = advertisementPost.title,
            cost = advertisementPost.cost
        )
        advertisementRepository.save(advertisement)

        val event = AdvertisementCreatedEvent(
            title = advertisement.title!!,
            cost = advertisement.cost!!
        )
        applicationEventPublisher.publishEvent(event)

        logger.info("광고 등록 완료")
    }
}
