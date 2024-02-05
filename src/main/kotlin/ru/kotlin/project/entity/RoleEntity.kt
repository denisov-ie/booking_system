package ru.kotlin.project.entity

import javax.persistence.*

@Entity
@Table(name = "roles")
data class RoleEntity  (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "role_id")
    var roleId: Long = 0,

    var role: String? = ""
)

{
    override fun toString(): String {
        return "Role(roleId=$roleId, role=$role)"
    }
}