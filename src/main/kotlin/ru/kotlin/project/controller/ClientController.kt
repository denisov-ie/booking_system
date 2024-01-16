package ru.kotlin.project.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import ru.kotlin.project.entity.ClientEntity
import ru.kotlin.project.service.ClientService

@RestController
@RequestMapping("/api/client")
class ClientController @Autowired constructor(private val clientService: ClientService) {
    @PostMapping("/add")
    fun add(@RequestBody entity: ClientEntity): ResponseEntity<ClientEntity> {
        return clientService.add(entity)
    }

    @GetMapping("/list")
    fun list(): ResponseEntity<List<ClientEntity>> {
        return clientService.list()
    }

    @GetMapping("{entityId}/get")
    fun view(@PathVariable entityId: Long): ResponseEntity<ClientEntity> {
        return clientService.get(entityId)
    }

    @PutMapping("{entityId}/edit")
    fun edit(@PathVariable entityId: Long, @RequestBody entry: ClientEntity): ResponseEntity<ClientEntity> {
        return clientService.edit(entityId, entry)
    }

    @DeleteMapping("/{entityId}/delete")
    fun delete(@PathVariable entityId: Long): ResponseEntity<ClientEntity> {
        return clientService.delete(entityId)
    }
}