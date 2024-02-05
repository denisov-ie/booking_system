package ru.kotlin.project.dto

data class BookingParametersDto constructor(
    var clientId: Long? = 0,
    var operationId: Long? = 0,
    var timeSlotId: Long? = 0
)