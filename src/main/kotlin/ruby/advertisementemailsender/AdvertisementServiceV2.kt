package ruby.advertisementemailsender

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ruby.advertisementemailsender.event.AdvertisementEventService

@Service
class AdvertisementServiceV2(
    private val advertisementRepository: AdvertisementRepository,
    private val advertisementEventService: AdvertisementEventService
) {

    @Transactional
    fun add(advertisementPost: AdvertisementPost) {
        val advertisement = Advertisement(
            title = advertisementPost.title,
            cost = advertisementPost.cost
        )
        val saveAdvertisement = advertisementRepository.save(advertisement)

        advertisementEventService.sendMail(saveAdvertisement)
    }
}
