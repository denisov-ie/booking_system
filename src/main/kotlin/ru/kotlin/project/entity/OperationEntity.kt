package ru.kotlin.project.entity

import org.hibernate.annotations.NaturalId
import javax.persistence.*

@Entity
@Table(name = "operations")
data class OperationEntity (
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    @Column(name = "operation_id")
    var operationId: Long = 0,

    var title: String,

    var description: String? = "",

    var duration: Int
) {
    override fun toString(): String {
        return "Service(operationId=$operationId, title=$title, description=$description, duration=$duration)"
    }
}