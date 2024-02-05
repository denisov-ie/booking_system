package ru.kotlin.project.controller.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import ru.kotlin.project.entity.ClientEntity
import ru.kotlin.project.service.ClientService

@RestController
@RequestMapping("/api/client")
class ClientController @Autowired constructor(
    private val clientService: ClientService
    )

{
    @PostMapping("/add")
    fun add(@RequestBody entity: ClientEntity): ResponseEntity<ClientEntity> {
        return clientService.add(entity)
    }

    @GetMapping("/list")
    fun list(): ResponseEntity<List<ClientEntity>> {
        return clientService.list()
    }

    @GetMapping("{clientId}/get")
    fun view(@PathVariable clientId: Long): ResponseEntity<ClientEntity> {
        return clientService.get(clientId)
    }

    @PutMapping("{clientId}/edit")
    fun edit(@PathVariable clientId: Long, @RequestBody entity: ClientEntity): ResponseEntity<ClientEntity> {
        return clientService.edit(clientId, entity)
    }

    @DeleteMapping("/{clientId}/delete")
    @PreAuthorize("hasAuthority('SUPPORT') || hasAuthority('ADMIN')")
    fun delete(@PathVariable clientId: Long): ResponseEntity<ClientEntity> {
        return clientService.delete(clientId)
    }
}