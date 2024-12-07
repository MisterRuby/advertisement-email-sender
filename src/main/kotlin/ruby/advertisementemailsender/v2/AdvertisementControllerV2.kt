package ruby.advertisementemailsender.v2

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ruby.advertisementemailsender.AdvertisementPost

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
