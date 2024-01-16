package ru.kotlin.project.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ru.kotlin.project.entity.OperationEntity
import ru.kotlin.project.repository.OperationRepository

@Service
class OperationService(@Autowired private val operationRepository: OperationRepository) {

    fun list(): ResponseEntity<List<OperationEntity>> {
        return ResponseEntity(operationRepository.findAll().toList(), HttpStatus.OK)
    }

    fun get(entityId: Long): ResponseEntity<OperationEntity> {
        val targetEntity = operationRepository.findById(entityId).orElse(null) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(targetEntity, HttpStatus.OK)
    }

    fun add(entity: OperationEntity): ResponseEntity<OperationEntity> {
        operationRepository.save(entity)
        return ResponseEntity(entity, HttpStatus.CREATED)
    }

    fun edit(entityId: Long, entity: OperationEntity): ResponseEntity<OperationEntity> {
        val targetEntity = operationRepository.findById(entityId).orElse(null) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        val updatedEntity = targetEntity.copy(title = entity.title, description = entity.description)
        operationRepository.save(updatedEntity)
        return ResponseEntity(updatedEntity, HttpStatus.OK)
    }

    fun delete(entityId: Long): ResponseEntity<OperationEntity> {
        if (!operationRepository.existsById(entityId)) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        operationRepository.deleteById(entityId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}