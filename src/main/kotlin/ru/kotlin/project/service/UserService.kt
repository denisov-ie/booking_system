package ru.kotlin.project.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import ru.kotlin.project.entity.UserEntity
import ru.kotlin.project.repository.UserRepository

@Service
class UserService @Autowired constructor(
    private val userRepository: UserRepository
): UserDetailsService

{
    override fun loadUserByUsername(login: String?): UserDetails {
        return userRepository.findByLogin(login) ?: throw UsernameNotFoundException(login)
    }

    fun add(entity: UserEntity?): ResponseEntity<UserEntity> {
        if (entity == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        userRepository.save(entity)
        return ResponseEntity(entity, HttpStatus.CREATED)
    }

    fun list(): ResponseEntity<List<UserEntity>> {
        return ResponseEntity(userRepository.findAll().toList(), HttpStatus.OK)
    }

    fun get(userId: Long?): ResponseEntity<UserEntity> {
        if (userId == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        val targetEntity = userRepository.findById(userId).orElse(null) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(targetEntity, HttpStatus.OK)
    }

    fun edit(userId: Long?, entity: UserEntity?): ResponseEntity<UserEntity> {
        if (userId == null || entity == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        val targetEntity = userRepository.findById(userId).orElse(null) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        val updatedEntity = targetEntity.copy(pass = entity.pass, roleEntity = entity.roleEntity)
        userRepository.save(updatedEntity)
        return ResponseEntity(updatedEntity, HttpStatus.OK)
    }

    fun delete(userId: Long?): ResponseEntity<UserEntity> {
        if (userId == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        if (!userRepository.existsById(userId)) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        userRepository.deleteById(userId)
        return ResponseEntity(HttpStatus.OK)
    }
}