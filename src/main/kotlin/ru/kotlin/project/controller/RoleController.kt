package ru.kotlin.project.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import ru.kotlin.project.entity.RoleEntity
import ru.kotlin.project.service.RoleService

@RestController
@RequestMapping("/api/role")
@PreAuthorize("hasAuthority('ADMIN')")
class RoleController @Autowired constructor(
    private val roleService: RoleService
    )

{
    @PostMapping("/add")
    fun add(@RequestBody entity: RoleEntity): ResponseEntity<RoleEntity> {
        return roleService.add(entity)
    }

    @GetMapping("/list")
    fun list(): ResponseEntity<List<RoleEntity>> {
        return roleService.list()
    }

    @GetMapping("{roleId}/get")
    fun view(@PathVariable roleId: Long): ResponseEntity<RoleEntity> {
        return roleService.get(roleId)
    }

    @PutMapping("{roleId}/edit")
    fun edit(@PathVariable roleId: Long, @RequestBody entity: RoleEntity): ResponseEntity<RoleEntity> {
        return roleService.edit(roleId, entity)
    }

    @DeleteMapping("/{roleId}/delete")
    fun delete(@PathVariable roleId: Long): ResponseEntity<RoleEntity> {
        return roleService.delete(roleId)
    }
}