package ru.kotlin.project.controller.app

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import ru.kotlin.project.dto.TimeParametersDto
import ru.kotlin.project.entity.TimeSlotEntity
import ru.kotlin.project.service.*
import java.sql.Date
import java.sql.Time

@Controller
@RequestMapping("/app/timeslot")
class TimeSlotPageController @Autowired constructor(
    private val timeSlotService: TimeSlotService,
    private val operationService: OperationService,
    private val clientService: ClientService
    )

{
    @GetMapping("/add")
    fun add(@ModelAttribute entity: TimeParametersDto, model: Model): String {
        model.addAttribute("entity", entity)
        model.addAttribute("operations", operationService.list().body)
        return "timeslot/add"
    }

    @PostMapping("/add")
    fun add(@ModelAttribute entity: TimeParametersDto): String {
        entity.operationEntity = operationService.get(entity.operationId).body
        timeSlotService.add(entity, false)
        return "redirect:/app/timeslot/list"
    }

    @GetMapping("/list")
    fun list(model: Model): String {
        model.addAttribute("entityList", timeSlotService.list().body)
        return "timeslot/list"
    }

    @GetMapping("{timeSlotId}/get")
    fun view(@PathVariable timeSlotId: Long, model: Model): String {
        model.addAttribute("entity", timeSlotService.get(timeSlotId).body)
        return "timeslot/get"
    }

    @GetMapping("{timeSlotId}/edit")
    fun edit(@PathVariable timeSlotId: Long, model: Model): String {
        val timeSlotEntity = timeSlotService.get(timeSlotId).body
        val timeSlotDtoEntity = TimeParametersDto(
            dateFor = Date.valueOf(timeSlotEntity?.dateFor.toString()),
            timeFrom = Time.valueOf(timeSlotEntity?.timeFrom.toString()),
            timeTo = Time.valueOf(timeSlotEntity?.timeTo.toString()),
            clientId = timeSlotEntity?.clientEntity?.clientId,
            operationId = timeSlotEntity?.operationEntity?.operationId,
            timeSlotId = timeSlotEntity?.timeSlotId,
            isLocked = timeSlotEntity?.isLocked
        )
        model.addAttribute("entity", timeSlotDtoEntity)
        model.addAttribute("clients", clientService.list().body)
        model.addAttribute("operations", operationService.list().body)
        return "timeslot/edit"
    }

    @PostMapping("{timeSlotId}/edit")
    fun edit(@PathVariable timeSlotId: Long, @ModelAttribute entity: TimeParametersDto): String {
        println(entity)
        val timeSlotEntity = TimeSlotEntity(
            timeSlotId = entity.timeSlotId!!,
            dateFor = entity.dateFor!!,
            timeFrom = entity.timeFrom!!,
            timeTo = entity.timeTo!!,
            isLocked = entity.isLocked!!,
            clientEntity = clientService.get(entity.clientId).body,
            operationEntity = operationService.get(entity.operationId).body!!
        )
        timeSlotService.edit(timeSlotId, timeSlotEntity)
        return "redirect:/app/timeslot/list"
    }

    @GetMapping("/{timeSlotId}/delete")
    @PreAuthorize("hasAuthority('SUPPORT') || hasAuthority('ADMIN')")
    fun delete(@PathVariable timeSlotId: Long): String {
        timeSlotService.delete(timeSlotId)
        return "redirect:/app/timeslot/list"
    }
}