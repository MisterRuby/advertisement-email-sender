package ruby.advertisementemailsender.v1

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ruby.advertisementemailsender.AdvertisementPost

@RestController
@RequestMapping("/api/advertisement/v1")
class AdvertisementControllerV1(
    private val advertisementServiceV1: AdvertisementServiceV1
) {

    @PostMapping
    fun postV1(@RequestBody advertisementPost: AdvertisementPost) {
        advertisementServiceV1.add(advertisementPost)
    }

}
