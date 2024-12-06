package ruby.advertisementemailsender

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdvertisementServiceV1(
    private val advertisementRepository: AdvertisementRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun add(advertisementPost: AdvertisementPost) {
        val advertisement = Advertisement(
            title = advertisementPost.title,
            cost = advertisementPost.cost
        )
        advertisementRepository.save(advertisement)

        sendMail()
    }

    @Throws(RuntimeException::class)
    fun sendMail() {
        throw RuntimeException("메일 전송 중 오류 발생!")
    }
}
