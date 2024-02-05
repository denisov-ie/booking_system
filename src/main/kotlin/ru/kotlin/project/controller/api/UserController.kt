package ru.kotlin.project.controller.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import ru.kotlin.project.dto.UserParametersDto
import ru.kotlin.project.entity.RoleEntity
import ru.kotlin.project.entity.UserEntity
import ru.kotlin.project.service.RoleService
import ru.kotlin.project.service.UserService

@RestController
@RequestMapping("/api/user")
@PreAuthorize("hasAuthority('ADMIN')")
class UserController @Autowired constructor(
    private val userService: UserService,
    private val roleService: RoleService
    )

{
    @PostMapping("/add")
    fun add(@RequestBody entity: UserParametersDto): ResponseEntity<UserEntity> {
        val roleEntity = roleService.get(entity.roleId)
        val userEntity: UserEntity?
        if (roleEntity.hasBody() && !entity.login.isNullOrEmpty()) {
            userEntity = UserEntity(
                login = entity.login!!,
                pass = PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(entity.pass),
                roleEntity = roleEntity.body
            )
        } else if (!entity.login.isNullOrEmpty()) {
            userEntity = UserEntity(
                login = entity.login!!,
                pass = PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(entity.pass)
            )
        } else {
            userEntity = null
        }
        return userService.add(userEntity)
    }

    @GetMapping("/list")
    fun list(): ResponseEntity<List<UserEntity>> {
        return userService.list()
    }

    @GetMapping("{userId}/get")
    fun view(@PathVariable userId: Long): ResponseEntity<UserEntity> {
        return userService.get(userId)
    }

    @PutMapping("{userId}/edit")
    fun edit(@PathVariable userId: Long, @RequestBody entity: UserEntity): ResponseEntity<UserEntity> {
        entity.pass = PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(entity.pass)
        return userService.edit(userId, entity)
    }

    @DeleteMapping("/{userId}/delete")
    fun delete(@PathVariable userId: Long): ResponseEntity<UserEntity> {
        return userService.delete(userId)
    }
}