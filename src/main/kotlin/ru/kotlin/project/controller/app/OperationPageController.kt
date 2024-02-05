package ru.kotlin.project.controller.app

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import ru.kotlin.project.entity.OperationEntity
import ru.kotlin.project.service.OperationService

@Controller
@RequestMapping("/app/operation")
class OperationPageController @Autowired constructor(
    private val operationService: OperationService
    )

{
    @GetMapping("/add")
    fun add(@ModelAttribute entity: OperationEntity, model: Model): String {
        model.addAttribute("entity", entity)
        return "operation/add"
    }

    @PostMapping("/add")
    fun add(@ModelAttribute entity: OperationEntity): String {
        operationService.add(entity)
        return "redirect:/app/operation/list"
    }

    @GetMapping("/list")
    fun list(model: Model): String {
        model.addAttribute("entityList", operationService.list().body)
        return "operation/list"
    }

    @GetMapping("{operationId}/get")
    fun view(@PathVariable operationId: Long, model: Model): String {
        model.addAttribute("entity", operationService.get(operationId).body)
        return "operation/get"
    }

    @GetMapping("{operationId}/edit")
    fun edit(@PathVariable operationId: Long, model: Model): String {
        model.addAttribute("entity", operationService.get(operationId).body)
        return "operation/edit"
    }

    @PostMapping("{operationId}/edit")
    fun edit(@PathVariable operationId: Long, @ModelAttribute entity: OperationEntity): String {
        operationService.edit(operationId, entity)
        return "redirect:/app/operation/list"
    }

    @GetMapping("/{operationId}/delete")
    @PreAuthorize("hasAuthority('SUPPORT') || hasAuthority('ADMIN')")
    fun delete(@PathVariable operationId: Long): String {
        operationService.delete(operationId)
        return "redirect:/app/operation/list"
    }
}