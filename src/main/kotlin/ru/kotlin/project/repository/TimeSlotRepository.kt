package ru.kotlin.project.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.kotlin.project.entity.TimeSlotEntity

@Repository
interface TimeSlotRepository: CrudRepository<TimeSlotEntity, Long>