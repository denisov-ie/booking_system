package ru.kotlin.project.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ru.kotlin.project.entity.ClientEntity
import ru.kotlin.project.repository.ClientRepository

@Service
class ClientService @Autowired constructor(
    private val clientRepository: ClientRepository
    )

{
    fun add(entity: ClientEntity?): ResponseEntity<ClientEntity> {
        if (entity == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        clientRepository.save(entity)
        return ResponseEntity(entity, HttpStatus.CREATED)
    }

    fun list(): ResponseEntity<List<ClientEntity>> {
        return ResponseEntity(clientRepository.findAll().toList(), HttpStatus.OK)
    }

    fun get(clientId: Long?): ResponseEntity<ClientEntity> {
        if (clientId == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        val targetEntity = clientRepository.findById(clientId).orElse(null) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(targetEntity, HttpStatus.OK)
    }

    fun edit(clientId: Long?, entity: ClientEntity?): ResponseEntity<ClientEntity> {
        if (clientId == null || entity == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        val targetEntity = clientRepository.findById(clientId).orElse(null) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        val updatedEntity = targetEntity.copy(name = entity.name, email = entity.email, phone = entity.phone)
        clientRepository.save(updatedEntity)
        return ResponseEntity(updatedEntity, HttpStatus.OK)
    }

    fun delete(clientId: Long?): ResponseEntity<ClientEntity> {
        if (clientId == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        if (!clientRepository.existsById(clientId)) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        clientRepository.deleteById(clientId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}