package ru.kotlin.project.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import ru.kotlin.project.dto.BookingParametersDto
import ru.kotlin.project.dto.TimeParametersDto
import ru.kotlin.project.entity.TimeSlotEntity
import ru.kotlin.project.service.ClientService
import ru.kotlin.project.service.OperationService
import ru.kotlin.project.service.TimeSlotService
import java.sql.Time
import java.text.SimpleDateFormat

@RestController
@RequestMapping("/api/booking")
class BookingController @Autowired constructor(
    private val clientService: ClientService,
    private val timeSlotService: TimeSlotService,
    private val operationService: OperationService
    )

{
    @PostMapping("/add")
    fun add(@RequestBody entity: BookingParametersDto): ResponseEntity<*> {
        val targetClientEntity = clientService.get(entity.clientId)
        val targetTimeSlotEntity = timeSlotService.get(entity.timeSlotId)
        if (targetClientEntity.hasBody() && targetTimeSlotEntity.hasBody()) {
            val updatedTimeSlotEntity = targetTimeSlotEntity.body?.copy(clientEntity = targetClientEntity.body)
            return timeSlotService.edit(entity.timeSlotId, updatedTimeSlotEntity)
        }
        if (!targetClientEntity.hasBody()) {
            return targetClientEntity
        }
        return targetTimeSlotEntity
    }

    @GetMapping("{operationId}/available")
    fun available(@PathVariable operationId: Long): ResponseEntity<List<TimeSlotEntity>> {
        return timeSlotService.list(true, true, operationId, null)
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
        val targetTimeSlotEntity = timeSlotService.get(timeSlotId)
        if (targetTimeSlotEntity.hasBody()) {
            val updatedTimeSlotEntity = targetTimeSlotEntity.body!!.copy(clientEntity = null)
            return timeSlotService.edit(timeSlotId, updatedTimeSlotEntity)
        }
        return targetTimeSlotEntity
    }

    @PutMapping("{operationId}/open")
    fun open(@PathVariable operationId: Long, @RequestBody entity: TimeParametersDto): ResponseEntity<*> {
        val operationEntity = operationService.get(operationId)
        if (operationEntity.hasBody()) {
            entity.operationEntity = operationEntity.body
            return timeSlotService.add(entity, true)
        }
        return operationEntity
    }
}