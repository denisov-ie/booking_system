package ru.kotlin.project.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "timeslots")
data class TimeSlotEntity (
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    var timeSlotId: Long = 0,

    var date: Date,

    var timeFrom: Date,

    var timeTo: Date,

    var isLocked: Boolean,

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinColumn(name = "operationId")
    var operationEntity: OperationEntity,

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinColumn(name = "clientId")
    var clientEntity: ClientEntity
) {
    override fun toString(): String {
        return "TimeSlot(timeSlotId=$timeSlotId, date=$date, timeFrom=$timeFrom, timeTo=$timeTo)"
    }
}