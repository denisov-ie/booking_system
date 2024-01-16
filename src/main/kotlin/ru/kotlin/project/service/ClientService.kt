package ru.kotlin.project.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ru.kotlin.project.entity.ClientEntity
import ru.kotlin.project.repository.ClientRepository

@Service
class ClientService(@Autowired private val clientRepository: ClientRepository) {

    fun list(): ResponseEntity<List<ClientEntity>> {
        return ResponseEntity(clientRepository.findAll().toList(), HttpStatus.OK)
    }

    fun get(entityId: Long): ResponseEntity<ClientEntity> {
        val targetEntity = clientRepository.findById(entityId).orElse(null) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(targetEntity, HttpStatus.OK)
    }

    fun add(entity: ClientEntity): ResponseEntity<ClientEntity> {
        clientRepository.save(entity)
        return ResponseEntity(entity, HttpStatus.CREATED)
    }

    fun edit(entityId: Long, entity: ClientEntity): ResponseEntity<ClientEntity> {
        val targetEntity = clientRepository.findById(entityId).orElse(null) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        val updatedEntity = targetEntity.copy(name = entity.name, email = entity.email, phone = entity.phone)
        clientRepository.save(updatedEntity)
        return ResponseEntity(updatedEntity, HttpStatus.OK)
    }

    fun delete(entityId: Long): ResponseEntity<ClientEntity> {
        if (!clientRepository.existsById(entityId)) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        clientRepository.deleteById(entityId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}