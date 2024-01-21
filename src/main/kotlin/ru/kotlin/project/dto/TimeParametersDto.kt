package ru.kotlin.project.dto

import ru.kotlin.project.entity.OperationEntity
import java.sql.Date
import java.sql.Time

data class TimeParametersDto constructor(
    var operationEntity: OperationEntity?,
    var dateFor: Date,
    var timeFrom: Time,
    var timeTo: Time
)