package ru.kotlin.project.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ru.kotlin.project.dto.TimeParametersDto
import ru.kotlin.project.entity.OperationEntity
import ru.kotlin.project.entity.TimeSlotEntity
import ru.kotlin.project.repository.OperationRepository
import ru.kotlin.project.repository.TimeSlotRepository
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

@Service
class OperationService @Autowired constructor(
    private val operationRepository: OperationRepository
    )

{
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

    fun get(operationId: Long?): ResponseEntity<OperationEntity> {
        if (operationId == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        val targetEntity = operationRepository.findById(operationId).orElse(null) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(targetEntity, HttpStatus.OK)
    }

    fun edit(operationId: Long?, entity: OperationEntity?): ResponseEntity<OperationEntity> {
        if (operationId == null || entity == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        val targetEntity = operationRepository.findById(operationId).orElse(null) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        val updatedEntity = targetEntity.copy(title = entity.title, description = entity.description, duration = entity.duration)
        operationRepository.save(updatedEntity)
        return ResponseEntity(updatedEntity, HttpStatus.OK)
    }

    fun delete(operationId: Long?): ResponseEntity<OperationEntity> {
        if (operationId == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        if (!operationRepository.existsById(operationId)) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        operationRepository.deleteById(operationId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}