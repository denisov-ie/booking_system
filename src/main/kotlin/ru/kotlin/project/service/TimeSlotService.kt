package ru.kotlin.project.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ru.kotlin.project.entity.TimeSlotEntity
import ru.kotlin.project.repository.TimeSlotRepository

@Service
class TimeSlotService(@Autowired private val timeSlotRepository: TimeSlotRepository) {

    fun list(): ResponseEntity<List<TimeSlotEntity>> {
        return ResponseEntity(timeSlotRepository.findAll().toList(), HttpStatus.OK)
    }

    fun get(entityId: Long): ResponseEntity<TimeSlotEntity> {
        val targetEntity = timeSlotRepository.findById(entityId).orElse(null) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(targetEntity, HttpStatus.OK)
    }

    fun add(entity: TimeSlotEntity): ResponseEntity<TimeSlotEntity> {
        timeSlotRepository.save(entity)
        return ResponseEntity(entity, HttpStatus.CREATED)
    }

    fun edit(entityId: Long, entity: TimeSlotEntity): ResponseEntity<TimeSlotEntity> {
        val targetEntity = timeSlotRepository.findById(entityId).orElse(null) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        val updatedEntity = targetEntity.copy(isLocked = entity.isLocked)
        timeSlotRepository.save(updatedEntity)
        return ResponseEntity(updatedEntity, HttpStatus.OK)
    }
}