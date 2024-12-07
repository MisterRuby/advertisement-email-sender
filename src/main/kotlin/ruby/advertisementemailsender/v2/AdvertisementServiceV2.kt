package ruby.advertisementemailsender.v2

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ruby.advertisementemailsender.Advertisement
import ruby.advertisementemailsender.AdvertisementPost
import ruby.advertisementemailsender.AdvertisementRepository

@Service
class AdvertisementServiceV2(
    private val advertisementRepository: AdvertisementRepository
) {
    private val logger = org.slf4j.LoggerFactory.getLogger(javaClass)

    @Transactional
    fun add(advertisementPost: AdvertisementPost) {
        val advertisement = Advertisement(
            title = advertisementPost.title,
            cost = advertisementPost.cost
        )
        advertisementRepository.save(advertisement)

        logger.info("광고 등록 완료")
    }

    @Throws(RuntimeException::class)
    fun sendMail() {
        Thread.sleep(3000)      // 메일 전송 처리 시간
        throw RuntimeException("메일 전송 중 오류 발생!")
    }
}
