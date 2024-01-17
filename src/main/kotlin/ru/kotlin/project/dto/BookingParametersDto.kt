package ru.kotlin.project.dto

data class BookingParametersDto constructor(
    var clientId: Long = -1,
    var operationId: Long = -1,
    var timeSlotId: Long = -1
)