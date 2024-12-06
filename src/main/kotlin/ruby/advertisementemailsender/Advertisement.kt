package ruby.advertisementemailsender

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class Advertisement (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var title: String? = null,
    var cost: Long? = null,
    var startDate: LocalDateTime? = null,
    var endDate: LocalDateTime? = null
)
