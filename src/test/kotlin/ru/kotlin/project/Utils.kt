package ru.kotlin.project

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import ru.kotlin.project.entity.RoleEntity
import ru.kotlin.project.entity.UserEntity
import ru.kotlin.project.service.RoleService
import ru.kotlin.project.service.UserService

@Component
class Utils {
    @Autowired
    private lateinit var roleService: RoleService
    private lateinit var userRole: RoleEntity
    private lateinit var adminRole: RoleEntity
    private var userRoleId: Long = -1
    private var adminRoleId: Long = -1

    @Autowired
    private lateinit var userService: UserService
    private lateinit var admin: UserEntity
    private lateinit var user: UserEntity
    private var userId: Long = -1
    private var adminId: Long = -1

    public fun createUsers() {
        userRole = RoleEntity(
            role = "USER"
        )
        adminRole = RoleEntity(
            role = "ADMIN"
        )
        userRoleId = roleService.add(userRole).body?.roleId ?: -1
        userRole.roleId = userRoleId
        adminRoleId = roleService.add(adminRole).body?.roleId ?: -1
        user = UserEntity(
            login = "MANAGER",
            pass = "{bcrypt}\$2a\$10\$8TsZU49ieF9hmnQRRpvB7OAOZb6cRyJgpMQw3mvtMt7RyYu3iB6ly",
            roleEntity = userRole
        )
        admin = UserEntity(
            login = "ADMIN",
            pass = "{bcrypt}\$2a\$10\$LIe2vrJgjwLaIwFtuSNvquXzQF9Zc2BoLRSeWsmUc/.fga0n489Bm",
            roleEntity = adminRole
        )
        userId = userService.add(user).body?.userId ?: -1
        adminId = userService.add(admin).body?.userId ?: -1
    }

    public fun dropUsers() {
        userService.delete(userId)
        userService.delete(adminId)
        roleService.delete(userRoleId)
        roleService.delete(adminRoleId)
    }

    public fun loginUser(): HttpHeaders {
       return authHeader("MANAGER", "test")
    }

    public fun loginAdmin(): HttpHeaders {
        return authHeader("ADMIN", "test")
    }

    public fun loginNone(): HttpHeaders {
        return authHeader("", "")
    }

    private fun authHeader(username: String, password: String): HttpHeaders {
        val headers = HttpHeaders()
        headers.setBasicAuth(username, password)
        return headers
    }
}

data class ExtendedUserEntity (
    var userId: Long = 0,
    var login: String,
    var pass: String,
    var roleEntity: RoleEntity?,
    var password: String?,
    var username: String?,
    var authorities: Set<Authority>?,
    var accountNonExpired: Boolean?,
    var accountNonLocked: Boolean?,
    var credentialsNonExpired: Boolean?,
    var enabled: Boolean?
)

data class Authority (
    var authority: String?
)