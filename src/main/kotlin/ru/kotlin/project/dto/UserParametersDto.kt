package ru.kotlin.project.dto

data class UserParametersDto constructor(
    var login: String,
    var pass: String,
    var roleId: Long? = null
)