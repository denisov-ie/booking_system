package ru.kotlin.project.controller.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import ru.kotlin.project.dto.TimeParametersDto
import ru.kotlin.project.entity.TimeSlotEntity
import ru.kotlin.project.service.OperationService
import ru.kotlin.project.service.TimeSlotService

@RestController
@RequestMapping("/api/timeslot")
class TimeSlotController @Autowired constructor(
    private val timeSlotService: TimeSlotService,
    private val operationService: OperationService
    )

{
    @PutMapping("{operationId}/add")
    fun add(@PathVariable operationId: Long, @RequestBody entity: TimeParametersDto): ResponseEntity<*> {
        val operationEntity = operationService.get(operationId)
        if (operationEntity.hasBody()) {
            entity.operationEntity = operationEntity.body
            return timeSlotService.add(entity, false)
        }
        return operationEntity
    }

    @GetMapping("/list")
    fun list(): ResponseEntity<List<TimeSlotEntity>> {
        return timeSlotService.list()
    }

    @GetMapping("{timeSlotId}/get")
    fun view(@PathVariable timeSlotId: Long): ResponseEntity<TimeSlotEntity> {
        return timeSlotService.get(timeSlotId)
    }

    @PutMapping("{timeSlotId}/edit")
    fun edit(@PathVariable timeSlotId: Long, @RequestBody entity: TimeSlotEntity): ResponseEntity<TimeSlotEntity> {
        return timeSlotService.edit(timeSlotId, entity)
    }

    @DeleteMapping("/{timeSlotId}/delete")
    @PreAuthorize("hasAuthority('SUPPORT') || hasAuthority('ADMIN')")
    fun delete(@PathVariable timeSlotId: Long): ResponseEntity<TimeSlotEntity> {
        return timeSlotService.delete(timeSlotId)
    }
}