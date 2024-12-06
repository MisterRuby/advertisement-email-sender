package ruby.advertisementemailsender.event

import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class AdvertisementEventListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleAdvertisementCreatedEvent(event: AdvertisementCreatedEvent) {
        sendMail()
    }

    private fun sendMail() {
        throw RuntimeException("메일 전송 중 오류 발생!")
    }
}
