package ru.kotlin.project.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.kotlin.project.entity.UserEntity

@Repository
interface UserRepository: JpaRepository<UserEntity, Long> {
    fun findByLogin(login: String?): UserEntity
}