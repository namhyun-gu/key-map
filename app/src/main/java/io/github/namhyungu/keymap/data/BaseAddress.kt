package io.github.namhyungu.keymap.data

sealed class BaseAddress {
    abstract val type: Int
    abstract val province: String
    abstract val city: String
    abstract val address1: String
    abstract val address2: String

    abstract fun toMap(): Map<String, Any>

    companion object {
        const val TYPE_ADDRESS = 1
        const val TYPE_ROAD_ADDRESS = 2

        fun fromMap(map: Map<String, Any>): BaseAddress {
            return when (val type = (map["type"] as Long).toInt()) {
                TYPE_ROAD_ADDRESS -> {
                    RoadAddress.builder {
                        province = map["province"] as String
                        city = map["city"] as String
                        address1 = map["address1"] as String
                        address2 = map["address2"] as String
                        roadName = map["roadName"] as String
                        roadNumber = map["roadNumber"] as Long
                        buildingName = map["buildingName"] as String
                    }
                }
                TYPE_ADDRESS -> {
                    Address.builder {
                        province = map["province"] as String
                        city = map["city"] as String
                        address1 = map["address1"] as String
                        address2 = map["address2"] as String
                    }
                }
                else -> throw UnsupportedOperationException("Unsupported type: $type")
            }
        }
    }
}

data class Address(
    override val type: Int = TYPE_ADDRESS,
    override val province: String,
    override val city: String,
    override val address1: String,
    override val address2: String,
) : BaseAddress() {
    private constructor(builder: Builder) : this(
        province = builder.province,
        city = builder.city,
        address1 = builder.address1,
        address2 = builder.address2,
    )

    override fun toMap(): Map<String, Any> {
        return mapOf(
            "type" to type,
            "province" to province,
            "city" to city,
            "address1" to address1,
            "address2" to address2,
        )
    }

    class Builder {
        var province: String = ""
        var city: String = ""
        var address1: String = ""
        var address2: String = ""

        fun build() = Address(this)
    }

    companion object {
        fun builder(block: Builder.() -> Unit) =
            Builder().apply(block).build()
    }
}

data class RoadAddress(
    override val type: Int = TYPE_ROAD_ADDRESS,
    override val province: String,
    override val city: String,
    override val address1: String,
    override val address2: String,
    val roadName: String,
    val roadNumber: Long,
    val buildingName: String,
) : BaseAddress() {
    private constructor(builder: Builder) : this(
        province = builder.province,
        city = builder.city,
        address1 = builder.address1,
        address2 = builder.address2,
        roadName = builder.roadName,
        roadNumber = builder.roadNumber,
        buildingName = builder.buildingName,
    )

    override fun toMap(): Map<String, Any> {
        return mapOf(
            "type" to type,
            "province" to province,
            "city" to city,
            "address1" to address1,
            "address2" to address2,
            "roadName" to roadName,
            "roadNumber" to roadNumber,
            "buildingName" to buildingName,
        )
    }

    class Builder {
        var province: String = ""
        var city: String = ""
        var address1: String = ""
        var address2: String = ""
        var roadName: String = ""
        var roadNumber: Long = 0
        var buildingName: String = ""

        fun build() = RoadAddress(this)
    }

    companion object {
        fun builder(block: Builder.() -> Unit) =
            Builder().apply(block).build()
    }
}