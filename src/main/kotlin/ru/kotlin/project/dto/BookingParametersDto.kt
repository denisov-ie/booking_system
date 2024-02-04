package ru.kotlin.project.dto

data class BookingParametersDto constructor(
    var clientId: Long,
    var operationId: Long,
    var timeSlotId: Long
)