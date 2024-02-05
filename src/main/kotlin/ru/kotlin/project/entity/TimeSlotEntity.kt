package ru.kotlin.project.entity

import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.sql.Date
import java.sql.Time
import javax.persistence.*

@Entity
@Table(name = "timeslots")
data class TimeSlotEntity (
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "timeslot_id")
    var timeSlotId: Long = 0,

    @Column(name = "date_for")
    var dateFor: Date? = null,

    @Column(name = "time_from")
    var timeFrom: Time? = null,

    @Column(name = "time_to")
    var timeTo: Time? = null,

    @Column(name = "is_locked")
    var isLocked: Boolean = false,

    @ManyToOne(cascade = [CascadeType.MERGE], fetch = FetchType.EAGER)
    @JoinColumn(name = "operation_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    var operationEntity: OperationEntity,

    @ManyToOne(cascade = [CascadeType.MERGE], fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    var clientEntity: ClientEntity? = null
)

{
    override fun toString(): String {
        return "TimeSlot(timeSlotId=$timeSlotId, dateFor=$dateFor, timeFrom=$timeFrom, timeTo=$timeTo, isLocked=$isLocked)"
    }
}