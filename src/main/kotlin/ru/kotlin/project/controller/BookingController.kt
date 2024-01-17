package ru.kotlin.project.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import ru.kotlin.project.dto.BookingParametersDto
import ru.kotlin.project.entity.TimeSlotEntity
import ru.kotlin.project.service.ClientService
import ru.kotlin.project.service.TimeSlotService

@RestController
@RequestMapping("/api/booking")
class BookingController @Autowired constructor(private val clientService: ClientService, private val timeSlotService: TimeSlotService) {
    @PostMapping("/add")
    fun add(@RequestBody entity: BookingParametersDto): ResponseEntity<TimeSlotEntity> {
        val targetClientEntity = clientService.get(entity.clientId).body
        val targetTimeSlotEntity = timeSlotService.get(entity.timeSlotId).body
        val updatedTimeSlotEntity = targetTimeSlotEntity?.copy(clientEntity = targetClientEntity)
        return timeSlotService.edit(entity.timeSlotId, updatedTimeSlotEntity)
    }

    @GetMapping("{operationId}/available")
    fun available(@PathVariable operationId: Long): ResponseEntity<List<TimeSlotEntity>> {
        return timeSlotService.list(true, null, operationId, null)
    }

    @GetMapping("{operationId}/booked")
    fun booked(@PathVariable operationId: Long): ResponseEntity<List<TimeSlotEntity>> {
        return timeSlotService.list(false, true, operationId, null)
    }

    @GetMapping("{clientId}/active")
    fun active(@PathVariable clientId: Long): ResponseEntity<List<TimeSlotEntity>> {
        return timeSlotService.list(null, true, null, clientId)
    }

    @GetMapping("{timeSlotId}/remove")
    fun remove(@PathVariable timeSlotId: Long): ResponseEntity<TimeSlotEntity> {
        val targetTimeSlotEntity = timeSlotService.get(timeSlotId).body
        val updatedTimeSlotEntity = targetTimeSlotEntity?.copy(clientEntity = null)
        return timeSlotService.edit(timeSlotId, updatedTimeSlotEntity)
    }
}