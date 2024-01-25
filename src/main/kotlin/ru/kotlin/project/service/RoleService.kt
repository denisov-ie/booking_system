package ru.kotlin.project.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ru.kotlin.project.entity.RoleEntity
import ru.kotlin.project.repository.RoleRepository

@Service
class RoleService @Autowired constructor(
    private val roleRepository: RoleRepository
)

{
    fun add(entity: RoleEntity?): ResponseEntity<RoleEntity> {
        if (entity == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        roleRepository.save(entity)
        return ResponseEntity(entity, HttpStatus.CREATED)
    }

    fun list(): ResponseEntity<List<RoleEntity>> {
        return ResponseEntity(roleRepository.findAll().toList(), HttpStatus.OK)
    }

    fun get(roleId: Long?): ResponseEntity<RoleEntity> {
        if (roleId == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        val targetEntity = roleRepository.findById(roleId).orElse(null) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(targetEntity, HttpStatus.OK)
    }

    fun edit(roleId: Long?, entity: RoleEntity?): ResponseEntity<RoleEntity> {
        if (roleId == null || entity == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        val targetEntity = roleRepository.findById(roleId).orElse(null) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        val updatedEntity = targetEntity.copy(role = entity.role)
        roleRepository.save(updatedEntity)
        return ResponseEntity(updatedEntity, HttpStatus.OK)
    }

    fun delete(roleId: Long?): ResponseEntity<RoleEntity> {
        if (roleId == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        if (!roleRepository.existsById(roleId)) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        roleRepository.deleteById(roleId)
        return ResponseEntity(HttpStatus.OK)
    }
}