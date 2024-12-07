package ruby.advertisementemailsender.v3

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ruby.advertisementemailsender.AdvertisementPost

@RestController
@RequestMapping("/api/advertisement/v3")
class AdvertisementControllerV3(
    private val advertisementServiceV3: AdvertisementServiceV3
) {

    @PostMapping
    fun postV3(@RequestBody advertisementPost: AdvertisementPost) {
        advertisementServiceV3.add(advertisementPost)
    }
}
