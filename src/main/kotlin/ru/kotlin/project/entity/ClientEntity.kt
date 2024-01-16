package ru.kotlin.project.entity

import org.hibernate.annotations.NaturalId
import javax.persistence.*

@Entity
@Table(name = "clients")
data class ClientEntity (
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    var clientId: Long = 0,

    var name: String? = "",

    var email: String? = "",

    @NaturalId
    var phone: String
) {
    override fun toString(): String {
        return "Client(clientId=$clientId, name=$name, email=$email, phone=$phone)"
    }
}