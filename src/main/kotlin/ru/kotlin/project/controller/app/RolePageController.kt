package ru.kotlin.project.controller.app

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import ru.kotlin.project.entity.RoleEntity
import ru.kotlin.project.service.RoleService

@Controller
@RequestMapping("/app/role")
@PreAuthorize("hasAuthority('ADMIN')")
class RolePageController @Autowired constructor(
    private val roleService: RoleService
    )

{
    @GetMapping("/add")
    fun add(@ModelAttribute entity: RoleEntity, model: Model): String {
        model.addAttribute("entity", entity)
        return "role/add"
    }

    @PostMapping("/add")
    fun add(@ModelAttribute entity: RoleEntity): String {
        roleService.add(entity)
        return "redirect:/app/role/list"
    }

    @GetMapping("/list")
    fun list(model: Model): String {
        model.addAttribute("entityList", roleService.list().body)
        return "role/list"
    }

    @GetMapping("{roleId}/get")
    fun view(@PathVariable roleId: Long, model: Model): String {
        model.addAttribute("entity", roleService.get(roleId).body)
        return "role/get"
    }

    @GetMapping("{roleId}/edit")
    fun edit(@PathVariable roleId: Long, model: Model): String {
        model.addAttribute("entity", roleService.get(roleId).body)
        return "role/edit"
    }

    @PostMapping("{roleId}/edit")
    fun edit(@PathVariable roleId: Long, @ModelAttribute entity: RoleEntity): String {
        roleService.edit(roleId, entity)
        return "redirect:/app/role/list"
    }

    @GetMapping("/{roleId}/delete")
    fun delete(@PathVariable roleId: Long): String {
        roleService.delete(roleId)
        return "redirect:/app/role/list"
    }
}