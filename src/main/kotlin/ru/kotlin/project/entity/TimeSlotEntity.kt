package ru.kotlin.project.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "timeslots")
data class TimeSlotEntity (
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    @Column(name = "timeslot_id")
    var timeSlotId: Long = 0,

    @Column(name = "date_for")
    var dateFor: Date,

    @Column(name = "time_from")
    var timeFrom: Date,

    @Column(name = "time_to")
    var timeTo: Date,

    @Column(name = "is_locked")
    var isLocked: Boolean = false,

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinColumn(name = "operation_id")
    var operationEntity: OperationEntity,

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    var clientEntity: ClientEntity? = null
) {
    override fun toString(): String {
        return "TimeSlot(timeSlotId=$timeSlotId, dateFor=$dateFor, timeFrom=$timeFrom, timeTo=$timeTo, isLocked=$isLocked)"
    }
}