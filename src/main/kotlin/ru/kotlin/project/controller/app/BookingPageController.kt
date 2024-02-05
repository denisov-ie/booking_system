package ru.kotlin.project.controller.app

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import ru.kotlin.project.dto.BookingParametersDto
import ru.kotlin.project.dto.TimeParametersDto
import ru.kotlin.project.entity.TimeSlotEntity
import ru.kotlin.project.service.*
import java.sql.Date
import java.sql.Time

@Controller
@RequestMapping("/app/booking")
class BookingPageController @Autowired constructor(
    private val clientService: ClientService,
    private val timeSlotService: TimeSlotService,
    private val operationService: OperationService
    )

{
    @GetMapping("/list")
    fun list(@ModelAttribute entity: BookingParametersDto, model: Model): String {
        model.addAttribute("entity", entity)
        model.addAttribute("operations", operationService.list().body)
        model.addAttribute("clients", clientService.list().body)
        return "booking/list"
    }

    @PostMapping("/available")
    fun available(@ModelAttribute entity: BookingParametersDto, model: Model): String {
        model.addAttribute("entityList", timeSlotService.list(true, true, entity.operationId, null).body)
        return "booking/available"
    }

    @PostMapping("/booked")
    fun booked(@ModelAttribute entity: BookingParametersDto, model: Model): String {
        model.addAttribute("entityList", timeSlotService.list(false, true, entity.operationId, null).body)
        return "booking/booked"
    }

    @PostMapping("/active")
    fun active(@ModelAttribute entity: BookingParametersDto, model: Model): String {
        model.addAttribute("entityList", timeSlotService.list(null, true, null, entity.clientId).body)
        return "booking/active"
    }

    @GetMapping("{timeSlotId}/add")
    fun add(@PathVariable timeSlotId: Long, model: Model): String {
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
        return "booking/add"
    }

    @PostMapping("{timeSlotId}/add")
    fun add(@PathVariable timeSlotId: Long, @ModelAttribute entity: TimeParametersDto): String {
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
        return "redirect:/app/booking/list"
    }

    @GetMapping("{timeSlotId}/remove")
    fun remove(@PathVariable timeSlotId: Long, model: Model): String {
        val timeSlotEntity = timeSlotService.get(timeSlotId).body
        timeSlotEntity?.clientEntity = null
        timeSlotService.edit(timeSlotId, timeSlotEntity)
        return "redirect:/app/booking/list"
    }

    /*    @GetMapping("/add")
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
    }*/

    @GetMapping("/open")
    fun open(@ModelAttribute entity: TimeParametersDto, model: Model): String {
        model.addAttribute("entity", entity)
        model.addAttribute("operations", operationService.list().body)
        return "booking/open"
    }

    @PostMapping("/open")
    fun open(@ModelAttribute entity: TimeParametersDto): String {
        entity.operationEntity = operationService.get(entity.operationId).body
        timeSlotService.add(entity, true)
        return "redirect:/app/booking/list"
    }
}