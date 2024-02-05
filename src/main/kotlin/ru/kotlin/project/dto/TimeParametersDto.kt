package ru.kotlin.project.dto

import ru.kotlin.project.entity.OperationEntity
import java.sql.Date
import java.sql.Time

data class TimeParametersDto constructor(
    var operationEntity: OperationEntity? = null,
    var dateFor: Date? = null,
    var timeFrom: Time? = null,
    var timeTo: Time? = null,
    var clientId: Long? = 0,
    var operationId: Long? = 0,
    var timeSlotId: Long? = 0,
    var isLocked: Boolean? = null
)