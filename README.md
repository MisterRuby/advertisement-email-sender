# [Spring] ApplicationEventPublisher 를 통한 메일 전송 이벤트 처리

광고 관리 서비스 운영중에 광고 등록 기능이 되지 않는 내용을 담당자로부터 내용을 전달받았습니다. 문제를 분석하기 위해 로그를 조회한 결과 광고 등록 후 광고 등록 확인 메일을 전송하는 부분에서 오류가 발생되어 고객이 등록한 내용이 롤백되었음을 알 수 있었는데, 알고보니 메일 서버 IP 가 변경되어 문제가 발생한 것이었습니다. 변경된 IP 를 적용하는 것으로 문제는 일단락되었습니다.

## 그러나 아직 해결해야 될 문제가 남아있다.

고객에게 등록된 광고에 대해 확인 메일을 보내는 서비스에 문제가 있었다고 해서 광고 등록이 취소되어야 할까요?

1. 메일 서비스에 문제가 생길 경우 광고 등록도 취소될 경우 고객에게 공지한 후 광고를 재등록 요청한다.
    - 광고 등록과 확인 메일 전송 결과의 성공/실패가 일치되므로 관리가 편함
2. 메일 서비스에 문제가 생기더라도 광고 등록은 취소되지 않고 고객에게 매일 서비스 오류에 대해 공지
    - 메일이 전송되지 않더라도 서비스 화면에서 광고 등록을 알려주는 것은 유지 되어야 함
    - 광고 등록시 관련된 정보 입력의 양이 많이 때문에 재등록에 대한 불편함을 해소할 수 있음

위의 두 가지 방법 중 어떤 방법이 서비스를 사용하는 고객에게 있어서 괜찮을까 고민하다가 광고 등록시 입력받는 정보가 많아 오류로 인해 재등록을 요구하는 것이
고객에게 큰 불편함을 제공하는 것 같아 결국 2번의 방안을 선택하였습니다. 광고 등록시 화면에서 일차적으로 등록 확인을 알려주고 있었기 때문에 메일이 일시적으로 
전송되지 않더라도 *등록 확인* 의 측면에서는 크게 문제가 되지 않을 것이라 판단했습니다.

## 트랜잭션으로부터의 분리
```kotlin
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

@RestController
@RequestMapping("/api/advertisement/v2")
class AdvertisementControllerV2(
   private val advertisementServiceV2: AdvertisementServiceV2
) {

   @PostMapping
   fun postV2(@RequestBody advertisementPost: AdvertisementPost) {
      advertisementServiceV2.add(advertisementPost)
      advertisementServiceV2.sendMail()
   }

}
```
메일 전송이 광고 등록의 트랜잭션에 영향을 미치지 않도록 위와 같이 메일 전송 로직을 트랜잭션 밖에서 처리하도록 코드를 변경하였습니다.
예외가 발생 지점을 트랜잭션 밖으로 옮김으로서 롤백이 발생하지 않도록 하기 위함이었습니다.
![post-v2-log](/img/post-v2-log.png)
![post-v2-select](/img/post-v2-select.png)

의도한 대로 실행 결과 메일 전송이 실패했음에도 트랜잭션 롤백은 발생하지 않게되었습니다. 그러나 완전히 문제가 해결된 것은 아니었습니다. 
요청 내에서 오류가 발생했기 때문에 500 응답이 그대로 발생했는데 이는 예외가 요청 내에서 전파되었기 때문입니다.
![post-v2-response](/img/post-v2-response.png)

## ApplicationEventPublisher 를 통한 분리
ApplicationEventPublisher 는 이벤트를 발행하는 인터페이스입니다. 원하는 이벤트 처리를 메인로직과 분리하여 처리할 때 용이합니다.
주요 특징으로는 다음과 같습니다.
1. 이벤트 발행
   - `ApplicationEventPublisher`는 애플리케이션 내의 특정 상태 변화, 작업 완료, 알림이 필요한 상황 등을 표현하는 이벤트를 발행합니다. 
      일반적으로 애플리케이션 로직이 특정한 동작을 완료했을 때, 관련된 다른 구성 요소들에 이를 알리는 역할을 합니다.
2. 이벤트 기반 처리
   - 이벤트는 일반적으로 `ApplicationEvent`라는 클래스를 확장하여 정의되며, 이 클래스는 애플리케이션에서 발생할 수 있는 다양한 상태 변화나 작업을 나타냅니다.
   - 이벤트 수신자는 특정 이벤트가 발행될 때 실행되는 메소드를 포함하는 방식으로 구현됩니다.
3. 느슨한 결합
   - 이벤트 발행자와 수신자는 서로의 구체적인 존재를 몰라도 되며, 이벤트를 통해 간접적으로 상호작용하게 됩니다.
      이를 통해 시스템의 모듈화가 가능해지고, 변경에 유연하게 대응할 수 있습니다.
4. 동기 및 비동기 처리
   - 기본적으로 Spring의 이벤트는 동기적으로 처리되지만, 적절한 구성(`@Async` 어노테이션)을 통해 비동기적으로 처리될 수도 있습니다.
      비동기 처리 시, 이벤트 수신자의 실행이 발행자의 흐름과 분리되어 비즈니스 로직의 응답성이 향상될 수 있습니다.

```kotlin
// 1. ApplicationEventPublisher 를 통해 등록할 이벤트
class AdvertisementCreatedEvent (
   val title: String,
   val cost: Long
)

// 2. 이벤트가 등록될 때 실행될 Listener. 매개변수로 받는 이벤트가 등록되었을 때에 Listener 가 실행됨
@Component
class AdvertisementEventListener {
    
    // TransactionalEventListener 를 통해 이벤트를 등록한 트랜잭션 기준으로 어느 타이밍에 실행할지를 결정
    // 메일 전송 처리를 비동기로 처리하기 위해 @Async 적용
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

// 3. @Async 사용을 위한 @EnableAsync 적용
@SpringBootApplication
@EnableAsync
class AdvertisementEmailSenderApplication

fun main(args: Array<String>) {
    runApplication<AdvertisementEmailSenderApplication>(*args)
}

// 4. 광고 등록 후 메일 전송 이벤트를 등록.
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
```
ApplicationEventPublisher 를 적용하여 코드를 위와 같이 변경하였습니다.
이전과는 메일 전송시 예외가 발생해도 200으로 처리되었으며 비동기 처리로 인해 메일 전송 처리를 사용자가 기다리지 않아도 응답을 받을 수 있게 되었습니다.
![post-v3-log](/img/post-v3-log.png)
![post-v3-response](/img/post-v3-response.png)
