package ru.kotlin.project.entity

import javax.persistence.*

@Entity
@Table(name = "roles")
data class RoleEntity  (
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "role_id")
    var roleId: Long = 0,

    var role: String
)