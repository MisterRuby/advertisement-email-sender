package ruby.advertisementemailsender.v3.event

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class AdvertisementEventListener {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleAdvertisementCreatedEvent(event: AdvertisementCreatedEvent) {
        sendMail()
    }

    @Throws(RuntimeException::class)
    private fun sendMail() {
        Thread.sleep(3000)      // 메일 전송 처리 시간
        throw RuntimeException("메일 전송 중 오류 발생!")
    }
}
