package ru.kotlin.project.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ru.kotlin.project.entity.TimeSlotEntity
import ru.kotlin.project.repository.TimeSlotRepository
import java.util.*

@Service
class TimeSlotService(@Autowired private val timeSlotRepository: TimeSlotRepository) {
    fun add(entity: TimeSlotEntity?): ResponseEntity<TimeSlotEntity> {
        if (entity == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        timeSlotRepository.save(entity)
        return ResponseEntity(entity, HttpStatus.CREATED)
    }

    fun list(isAvailable: Boolean?, isActive: Boolean?, operationId: Long?, clientId: Long?): ResponseEntity<List<TimeSlotEntity>> {
        var timeSlots = timeSlotRepository.findAll().toList()
        if (isAvailable == true) {
            timeSlots = timeSlots.filter { entity -> entity.clientEntity == null && !entity.isLocked }
        } else if (isAvailable != null && isAvailable == false) {
            timeSlots = timeSlots.filter { entity -> entity.clientEntity != null && !entity.isLocked }
        }
        if (isActive == true) {
            val dateNow = Date()
            timeSlots = timeSlots.filter { entity -> entity.timeTo > dateNow }
        } else if (isActive != null && isActive == false) {
            val dateNow = Date()
            timeSlots = timeSlots.filter { entity -> entity.timeTo < dateNow }
        }
        if (operationId != null && operationId >= 0) {
            timeSlots = timeSlots.filter { entity -> entity.operationEntity.operationId == operationId }
        }
        if (clientId != null && clientId >= 0) {
            timeSlots = timeSlots.filter { entity -> entity.clientEntity != null && entity.clientEntity!!.clientId == clientId }
        }
        return ResponseEntity(timeSlots, HttpStatus.OK)
    }

    fun list(): ResponseEntity<List<TimeSlotEntity>> {
        return ResponseEntity(timeSlotRepository.findAll().toList(), HttpStatus.OK)
    }

    fun get(timeSlotId: Long): ResponseEntity<TimeSlotEntity> {
        val targetEntity = timeSlotRepository.findById(timeSlotId).orElse(null) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(targetEntity, HttpStatus.OK)
    }

    fun edit(timeSlotId: Long, entity: TimeSlotEntity?): ResponseEntity<TimeSlotEntity> {
        val targetEntity = timeSlotRepository.findById(timeSlotId).orElse(null) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        if (entity == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        val updatedEntity = targetEntity.copy(isLocked = entity.isLocked, clientEntity = entity.clientEntity)
        timeSlotRepository.save(updatedEntity)
        return ResponseEntity(updatedEntity, HttpStatus.OK)
    }
}