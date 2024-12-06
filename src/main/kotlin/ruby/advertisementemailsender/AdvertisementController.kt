package ruby.advertisementemailsender

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/advertisement")
class AdvertisementController(
    private val advertisementServiceV1: AdvertisementServiceV1,
    private val advertisementServiceV2: AdvertisementServiceV2
) {

    @PostMapping("/v1")
    fun postV1(@RequestBody advertisementPost: AdvertisementPost) {
        advertisementServiceV1.add(advertisementPost)
    }

    @PostMapping("/v2")
    fun postV2(@RequestBody advertisementPost: AdvertisementPost) {
        advertisementServiceV2.add(advertisementPost)
    }
}
