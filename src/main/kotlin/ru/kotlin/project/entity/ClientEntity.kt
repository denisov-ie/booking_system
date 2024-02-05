package ru.kotlin.project.entity

import javax.persistence.*

@Entity
@Table(name = "clients")
data class ClientEntity (
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "client_id")
    var clientId: Long = 0,

    var name: String? = "",

    var email: String? = "",

    var phone: String? = ""
)

{
    override fun toString(): String {
        return "Client(clientId=$clientId, name=$name, email=$email, phone=$phone)"
    }
}