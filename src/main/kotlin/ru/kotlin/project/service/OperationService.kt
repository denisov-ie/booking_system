package ru.kotlin.project.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ru.kotlin.project.entity.OperationEntity
import ru.kotlin.project.entity.TimeSlotEntity
import ru.kotlin.project.repository.OperationRepository
import ru.kotlin.project.repository.TimeSlotRepository
import java.text.SimpleDateFormat
import java.util.*

@Service
class OperationService(@Autowired private val operationRepository: OperationRepository, @Autowired private val timeSlotRepository: TimeSlotRepository) {
    fun add(entity: OperationEntity?): ResponseEntity<OperationEntity> {
        if (entity == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        operationRepository.save(entity)
        return ResponseEntity(entity, HttpStatus.CREATED)
    }

    fun list(): ResponseEntity<List<OperationEntity>> {
        return ResponseEntity(operationRepository.findAll().toList(), HttpStatus.OK)
    }

    fun get(operationId: Long): ResponseEntity<OperationEntity> {
        val targetEntity = operationRepository.findById(operationId).orElse(null) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(targetEntity, HttpStatus.OK)
    }

    fun open(operationId: Long, date: String?, timeFrom: String?, timeTo: String?): ResponseEntity<OperationEntity> {
        val targetEntity = operationRepository.findById(operationId).orElse(null) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        if (date == null || timeFrom == null || timeTo == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val dateToOpen = dateFormat.parse(date)
        var timestampFrom = dateTimeFormat.parse("$date $timeFrom").time
        val timestampTo = dateTimeFormat.parse("$date $timeTo").time
        val durationMs = targetEntity.duration * 60 * 1000
        while (timestampFrom < timestampTo - durationMs) {
            val tempTimestampFrom = timestampFrom
            timestampFrom += durationMs
            timeSlotRepository.save(TimeSlotEntity(
                date = dateToOpen,
                operationEntity = targetEntity,
                timeFrom = Date(tempTimestampFrom),
                timeTo = Date(timestampFrom)
                ))
        }
        return ResponseEntity(HttpStatus.OK)
    }

    fun edit(operationId: Long, entity: OperationEntity?): ResponseEntity<OperationEntity> {
        val targetEntity = operationRepository.findById(operationId).orElse(null) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        if (entity == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        val updatedEntity = targetEntity.copy(title = entity.title, description = entity.description)
        operationRepository.save(updatedEntity)
        return ResponseEntity(updatedEntity, HttpStatus.OK)
    }

    fun delete(operationId: Long): ResponseEntity<OperationEntity> {
        if (!operationRepository.existsById(operationId)) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        operationRepository.deleteById(operationId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}