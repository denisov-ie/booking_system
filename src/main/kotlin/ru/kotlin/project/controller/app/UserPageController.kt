package ru.kotlin.project.controller.app

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import ru.kotlin.project.dto.UserParametersDto
import ru.kotlin.project.entity.UserEntity
import ru.kotlin.project.service.RoleService
import ru.kotlin.project.service.UserService

@Controller
@RequestMapping("/app/user")
@PreAuthorize("hasAuthority('ADMIN')")
class UserPageController @Autowired constructor(
    private val userService: UserService,
    private val roleService: RoleService
    )

{
    @GetMapping("/add")
    fun add(@ModelAttribute entity: UserParametersDto, model: Model): String {
        model.addAttribute("entity", entity)
        model.addAttribute("roles", roleService.list().body)
        return "user/add"
    }

    @PostMapping("/add")
    fun add(@ModelAttribute entity: UserParametersDto): String {
        entity.pass = PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(entity.pass)
        val userEntity = UserEntity(
            login = entity.login!!,
            pass = entity.pass!!,
            roleEntity = roleService.get(entity.roleId).body
        )
        userService.add(userEntity)
        return "redirect:/app/user/list"
    }

    @GetMapping("/list")
    fun list(model: Model): String {
        model.addAttribute("entityList", userService.list().body)
        return "user/list"
    }

    @GetMapping("{userId}/get")
    fun view(@PathVariable userId: Long, model: Model): String {
        model.addAttribute("entity", userService.get(userId).body)
        return "user/get"
    }

    @GetMapping("{userId}/edit")
    fun edit(@PathVariable userId: Long, model: Model): String {
        val userEntity = userService.get(userId).body
        val userDtoEntity = UserParametersDto(
            userId = userId,
            login = userEntity!!.login,
            pass = "",
            roleId = userEntity.roleEntity?.roleId
        )
        model.addAttribute("entity", userDtoEntity)
        model.addAttribute("roles", roleService.list().body)
        return "user/edit"
    }

    @PostMapping("{userId}/edit")
    fun edit(@PathVariable userId: Long, @ModelAttribute entity: UserParametersDto): String {
        entity.pass = PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(entity.pass)
        val userEntity = UserEntity(
            userId = userId,
            login = entity.login!!,
            pass = entity.pass!!,
            roleEntity = roleService.get(entity.roleId).body
        )
        userService.edit(userId, userEntity)
        return "redirect:/app/user/list"
    }

    @GetMapping("/{userId}/delete")
    fun delete(@PathVariable userId: Long): String {
        userService.delete(userId)
        return "redirect:/app/user/list"
    }
}