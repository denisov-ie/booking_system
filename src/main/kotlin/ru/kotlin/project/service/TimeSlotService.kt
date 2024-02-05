package ru.kotlin.project.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ru.kotlin.project.dto.TimeParametersDto
import ru.kotlin.project.entity.TimeSlotEntity
import ru.kotlin.project.repository.TimeSlotRepository
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Service
class TimeSlotService @Autowired constructor(
    private val timeSlotRepository: TimeSlotRepository
    )

{
    fun add(entity: TimeParametersDto?, useDuration: Boolean?): ResponseEntity<Any> {
        if (entity?.operationEntity == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        if (useDuration != null && useDuration) {
            var timestampFrom = getTimestamp(entity.dateFor!!, entity.timeFrom!!)
            val timestampTo = getTimestamp(entity.dateFor!!, entity.timeTo!!)
            val durationMs = entity.operationEntity!!.duration!! * 60 * 1000
            val timeSlots = ArrayList<TimeSlotEntity>()
            while (timestampFrom < timestampTo - durationMs) {
                val tempTimestampFrom = timestampFrom
                timestampFrom += durationMs
                val timeSlotEntity = TimeSlotEntity(
                    dateFor = entity.dateFor!!,
                    timeFrom = Time(tempTimestampFrom),
                    timeTo = Time(timestampFrom),
                    operationEntity = entity.operationEntity!!
                )
                timeSlots.add(timeSlotEntity)
                timeSlotRepository.save(timeSlotEntity)
            }
            return ResponseEntity(timeSlots, HttpStatus.CREATED)
        } else {
            val timeSlotEntity = TimeSlotEntity(
                dateFor = entity.dateFor!!,
                timeFrom = entity.timeFrom!!,
                timeTo = entity.timeTo!!,
                operationEntity = entity.operationEntity!!)
            timeSlotRepository.save(timeSlotEntity)
            return ResponseEntity(timeSlotEntity, HttpStatus.CREATED)
        }
    }

    fun list(isAvailable: Boolean?, isActive: Boolean?, operationId: Long?, clientId: Long?): ResponseEntity<List<TimeSlotEntity>> {
        var timeSlots = timeSlotRepository.findAll().toList()
        if (isAvailable == true) {
            timeSlots = timeSlots.filter { entity -> entity.clientEntity == null && !entity.isLocked }
        } else if (isAvailable != null && isAvailable == false) {
            timeSlots = timeSlots.filter { entity -> entity.clientEntity != null && !entity.isLocked }
        }
        if (isActive == true) {
            val timestampNow = Date().time
            timeSlots = timeSlots.filter { entity -> getTimestamp(entity.dateFor!!, entity.timeTo!!) >= timestampNow }
        } else if (isActive != null && isActive == false) {
            val timestampNow = Date().time
            timeSlots = timeSlots.filter { entity -> getTimestamp(entity.dateFor!!, entity.timeTo!!) < timestampNow }
        }
        if (operationId != null && operationId >= 0) {
            timeSlots = timeSlots.filter { entity -> entity.operationEntity.operationId == operationId }
        }
        if (clientId != null && clientId >= 0) {
            timeSlots = timeSlots.filter { entity -> entity.clientEntity != null && entity.clientEntity!!.clientId == clientId }
        }
        return ResponseEntity(timeSlots, HttpStatus.OK)
    }

    fun list(): ResponseEntity<List<TimeSlotEntity>> {
        return ResponseEntity(timeSlotRepository.findAll().toList(), HttpStatus.OK)
    }

    fun get(timeSlotId: Long?): ResponseEntity<TimeSlotEntity> {
        if (timeSlotId == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        val targetEntity = timeSlotRepository.findById(timeSlotId).orElse(null) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(targetEntity, HttpStatus.OK)
    }

    fun edit(timeSlotId: Long?, entity: TimeSlotEntity?): ResponseEntity<TimeSlotEntity> {
        if (timeSlotId == null || entity == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        val targetEntity = timeSlotRepository.findById(timeSlotId).orElse(null) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        val updatedEntity = targetEntity.copy(
            isLocked = entity.isLocked,
            clientEntity = entity.clientEntity,
            dateFor = entity.dateFor,
            timeFrom = entity.timeFrom,
            timeTo = entity.timeTo,
            operationEntity = entity.operationEntity
        )
        timeSlotRepository.save(updatedEntity)
        return ResponseEntity(updatedEntity, HttpStatus.OK)
    }

    fun delete(timeSlotId: Long?): ResponseEntity<TimeSlotEntity> {
        if (timeSlotId == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        if (!timeSlotRepository.existsById(timeSlotId)) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        timeSlotRepository.deleteById(timeSlotId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    private fun getTimestamp(date: java.sql.Date, time: Time): Long {
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return dateTimeFormat.parse("$date $time").time
    }
}