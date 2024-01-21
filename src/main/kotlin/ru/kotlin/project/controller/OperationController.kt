package ru.kotlin.project.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import ru.kotlin.project.dto.TimeParametersDto
import ru.kotlin.project.entity.OperationEntity
import ru.kotlin.project.service.OperationService

@RestController
@RequestMapping("/api/operation")
class OperationController @Autowired constructor(private val operationService: OperationService) {
    @PostMapping("/add")
    fun add(@RequestBody entity: OperationEntity): ResponseEntity<OperationEntity> {
        return operationService.add(entity)
    }

    @GetMapping("/list")
    fun list(): ResponseEntity<List<OperationEntity>> {
        return operationService.list()
    }

    @GetMapping("{operationId}/get")
    fun view(@PathVariable operationId: Long): ResponseEntity<OperationEntity> {
        return operationService.get(operationId)
    }

    @PutMapping("{operationId}/open")
    fun open(@PathVariable operationId: Long, @RequestBody timeParameters: TimeParametersDto): ResponseEntity<TimeParametersDto> {
        return operationService.open(operationId, timeParameters)
    }

    @PutMapping("{operationId}/edit")
    fun edit(@PathVariable operationId: Long, @RequestBody entry: OperationEntity): ResponseEntity<OperationEntity> {
        return operationService.edit(operationId, entry)
    }

    @DeleteMapping("/{operationId}/delete")
    fun delete(@PathVariable operationId: Long): ResponseEntity<OperationEntity> {
        return operationService.delete(operationId)
    }
}