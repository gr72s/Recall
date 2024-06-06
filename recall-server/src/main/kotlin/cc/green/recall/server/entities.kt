package cc.green.recall.server

import jakarta.persistence.*
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@MappedSuperclass
abstract class AbstractEntity : Serializable {
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    @get:Id
    var id: Long? = 0

    @get:Column(name = "create_time")
    var createTime: LocalDateTime? = null

    @get:Column(name = "update_time")
    var updateTime: LocalDateTime? = null

    @PrePersist
    fun prePersist() {
        createTime = LocalDateTime.now()
        updateTime = LocalDateTime.now()
    }

    @PreUpdate
    fun preUpdate() {
        updateTime = LocalDateTime.now()
    }
}

@Entity
@Table(name = "pay_account")
class PayAccount : AbstractEntity() {
    // card issuer && card tail number
    @get:Column(name = "ac_identifier")
    var identifier: String? = null

    @get:Column(name = "ac_label")
    var label: String? = null

    @get:Column(name = "ac_type")
    var type: String? = null

    @get:Column(name = "ac_in_use")
    var inUse: Boolean = false

    @get:Transient
    @get:OneToMany(
        mappedBy = "payAccount",
        cascade = [(CascadeType.PERSIST)],
        orphanRemoval = false,
        fetch = FetchType.LAZY
    )
    var records = mutableListOf<ConsumeRecord>()
}

@Entity
@Table(name = "pay_platform")
class PayPlatform : AbstractEntity() {
    @get:Column(name = "pf_identifier")
    var identifier: String? = null

    @get:Column(name = "pf_label")
    var label: String? = null

    @get:Transient
    @get:OneToMany(
        mappedBy = "payPlatform",
        cascade = [CascadeType.PERSIST],
        orphanRemoval = false,
        fetch = FetchType.LAZY
    )
    var records = mutableSetOf<ConsumeRecord>()
}

@Entity
@Table(name = "cs_tag")
class ConsumeTag : AbstractEntity() {
    @get:Column(name = "tg_identifier")
    var identifier: String? = null

    @get:Column(name = "tg_label")
    var label: String? = null

    @get:Column(name = "tg_superior_tag")
    var superior: Long? = null

    @get:Transient
    @get:ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    var records = mutableSetOf<ConsumeRecord>()
}

@Entity
@Table(name = "cs_platform")
class ConsumePlatform : AbstractEntity() {
    @get:Column(name = "pf_identifier")
    var identifier: String? = null

    @get:Column(name = "pf_label")
    var label: String? = null

    @get:Transient
    @get:OneToMany(
        mappedBy = "consumePlatform",
        cascade = [CascadeType.PERSIST],
        orphanRemoval = false,
        fetch = FetchType.LAZY
    )
    var records = mutableSetOf<ConsumeRecord>()
}

@Entity
@Table(name = "cs_record")
class ConsumeRecord : AbstractEntity() {
    @get:ManyToOne(fetch = FetchType.LAZY)
    @get:JoinColumn(
        name = "pay_platform",
        foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    var payPlatform: PayPlatform? = null

    @get:Column(name = "cs_sum")
    var sum: BigDecimal = 0.toBigDecimal()

    @get:ManyToOne(fetch = FetchType.LAZY)
    @get:JoinColumn(
        name = "pay_account",
        foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    var payAccount: PayAccount? = null

    @get:Column(name = "cs_date")
    var consumeDate: LocalDate? = null

    @get:ManyToOne(fetch = FetchType.LAZY)
    @get:JoinColumn(
        name = "cs_platform",
        foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    var consumePlatform: ConsumePlatform? = null

    @get:ManyToMany(fetch = FetchType.LAZY)
    @get:JoinTable(
        name = "cs_record_tags",
        joinColumns = [(JoinColumn(name = "record_id"))],
        inverseJoinColumns = [(JoinColumn(name = "tag_id"))],
        foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    var tags: MutableSet<ConsumeTag> = mutableSetOf()
}