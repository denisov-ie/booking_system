package ru.kotlin.project.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import ru.kotlin.project.entity.TimeSlotEntity
import ru.kotlin.project.service.TimeSlotService

@RestController
@RequestMapping("/api/timeslot")
class TimeSlotController @Autowired constructor(private val timeSlotService: TimeSlotService) {
    @PostMapping("/add")
    fun add(@RequestBody entity: TimeSlotEntity): ResponseEntity<TimeSlotEntity> {
        return timeSlotService.add(entity)
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
    fun edit(@PathVariable timeSlotId: Long, @RequestBody entry: TimeSlotEntity): ResponseEntity<TimeSlotEntity> {
        return timeSlotService.edit(timeSlotId, entry)
    }
}