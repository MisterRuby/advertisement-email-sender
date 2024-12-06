package ruby.advertisementemailsender

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdvertisementService(
    private val advertisementRepository: AdvertisementRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun addV1(advertisementPost: AdvertisementPost) {
        val advertisement = Advertisement(
            title = advertisementPost.title,
            cost = advertisementPost.cost
        )
        advertisementRepository.save(advertisement)
        logger.info("광고 등록 완료!")

        // 2. 메일을 전송한다.
        sendMail()
        logger.info("메일 전송 완료!")
    }

    @Throws(RuntimeException::class)
    fun sendMail() {
        throw RuntimeException("메일 전송 중 오류 발생!")
    }
}
