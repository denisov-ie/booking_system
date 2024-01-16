package ru.kotlin.project.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import ru.kotlin.project.entity.OperationEntity
import ru.kotlin.project.service.OperationService

@RestController
@RequestMapping("/api/service")
class OperationController @Autowired constructor(private val operationService: OperationService) {
    @PostMapping("/add")
    fun add(@RequestBody entity: OperationEntity): ResponseEntity<OperationEntity> {
        return operationService.add(entity)
    }

    @GetMapping("/list")
    fun list(): ResponseEntity<List<OperationEntity>> {
        return operationService.list()
    }

    @GetMapping("{entityId}/get")
    fun view(@PathVariable entityId: Long): ResponseEntity<OperationEntity> {
        return operationService.get(entityId)
    }

    @PutMapping("{entityId}/edit")
    fun edit(@PathVariable entityId: Long, @RequestBody entry: OperationEntity): ResponseEntity<OperationEntity> {
        return operationService.edit(entityId, entry)
    }

    @DeleteMapping("/{entityId}/delete")
    fun delete(@PathVariable entityId: Long): ResponseEntity<OperationEntity> {
        return operationService.delete(entityId)
    }
}