package ru.kotlin.project.dto

data class UserParametersDto constructor(
    var userId: Long? = null,
    var login: String? = "",
    var pass: String? = "",
    var roleId: Long? = null
)