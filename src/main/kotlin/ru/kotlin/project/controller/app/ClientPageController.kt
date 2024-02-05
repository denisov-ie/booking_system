package ru.kotlin.project.controller.app

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import ru.kotlin.project.dto.UserParametersDto
import ru.kotlin.project.entity.ClientEntity
import ru.kotlin.project.entity.UserEntity
import ru.kotlin.project.service.ClientService
import ru.kotlin.project.service.RoleService
import ru.kotlin.project.service.UserService

@Controller
@RequestMapping("/app/client")
class ClientPageController @Autowired constructor(
    private val clientService: ClientService
    )

{
    @GetMapping("/add")
    fun add(@ModelAttribute entity: ClientEntity, model: Model): String {
        model.addAttribute("entity", entity)
        return "client/add"
    }

    @PostMapping("/add")
    fun add(@ModelAttribute entity: ClientEntity): String {
        clientService.add(entity)
        return "redirect:/app/client/list"
    }

    @GetMapping("/list")
    fun list(model: Model): String {
        model.addAttribute("entityList", clientService.list().body)
        return "client/list"
    }

    @GetMapping("{clientId}/get")
    fun view(@PathVariable clientId: Long, model: Model): String {
        model.addAttribute("entity", clientService.get(clientId).body)
        return "client/get"
    }

    @GetMapping("{clientId}/edit")
    fun edit(@PathVariable clientId: Long, model: Model): String {
        model.addAttribute("entity", clientService.get(clientId).body)
        return "client/edit"
    }

    @PostMapping("{clientId}/edit")
    fun edit(@PathVariable clientId: Long, @ModelAttribute entity: ClientEntity): String {
        clientService.edit(clientId, entity)
        return "redirect:/app/client/list"
    }

    @GetMapping("/{clientId}/delete")
    @PreAuthorize("hasAuthority('SUPPORT') || hasAuthority('ADMIN')")
    fun delete(@PathVariable clientId: Long): String {
        clientService.delete(clientId)
        return "redirect:/app/client/list"
    }
}