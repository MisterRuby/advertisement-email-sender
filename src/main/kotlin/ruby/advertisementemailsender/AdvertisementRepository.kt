package ruby.advertisementemailsender

import org.springframework.data.jpa.repository.JpaRepository

interface AdvertisementRepository : JpaRepository<Advertisement, Long>
