package ru.kotlin.project.service

import org.junit.jupiter.api.Assertions.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import ru.kotlin.project.Utils
import ru.kotlin.project.entity.RoleEntity

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class RoleServiceTest {
    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var roleService: RoleService
    private lateinit var roleEntityFirst: RoleEntity
    private lateinit var roleEntitySecond: RoleEntity

    @Autowired
    private lateinit var utils: Utils

    private fun url(path: String) = "http://localhost:${port}/api/role/${path}"

    @BeforeAll
    fun setUp() {
        utils.createUsers()
        roleEntityFirst = RoleEntity(
            role = "TEST_USER"
        )
        roleEntitySecond = RoleEntity(
            role = "TEST_ADMIN"
        )
    }

    @AfterAll
    fun tearDown() {
        roleService.delete(roleEntityFirst.roleId)
        utils.dropUsers()
    }

    @Test
    @Order(1)
    fun add() {
        val rs = restTemplate.exchange(
            url("add"),
            HttpMethod.POST,
            HttpEntity(roleEntityFirst, utils.loginAdmin()),
            object : ParameterizedTypeReference<RoleEntity>() {}
        )
        roleEntityFirst.roleId = rs.body!!.roleId
        assertEquals(HttpStatus.CREATED, rs.statusCode)
        assertEquals(roleEntityFirst, rs.body)
    }

    @Test
    @Order(2)
    fun list() {
        val rs = restTemplate.exchange(
            url("list"),
            HttpMethod.GET,
            HttpEntity<Nothing>(utils.loginAdmin()),
            object : ParameterizedTypeReference<List<RoleEntity>>() {}
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.OK)
        assertTrue(rs.body!!.stream().anyMatch { operation -> operation.roleId == roleEntityFirst.roleId })
    }

    @Test
    @Order(3)
    fun get() {
        val rs = restTemplate.exchange(
            url("${roleEntityFirst.roleId}/get"),
            HttpMethod.GET,
            HttpEntity<Nothing>(utils.loginAdmin()),
            object : ParameterizedTypeReference<RoleEntity>() {}
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(roleEntityFirst, rs.body)
    }

    @Test
    @Order(4)
    fun getWithWrongId() {
        val rs = restTemplate.exchange(
            url("-1/get"),
            HttpMethod.GET,
            HttpEntity<Nothing>(utils.loginAdmin()),
            object : ParameterizedTypeReference<RoleEntity>() {}
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    @Order(5)
    fun edit() {
        roleEntitySecond.roleId = roleEntityFirst.roleId
        val rs = restTemplate.exchange(
            url("${roleEntityFirst.roleId}/edit"),
            HttpMethod.PUT,
            HttpEntity(roleEntitySecond, utils.loginAdmin()),
            object : ParameterizedTypeReference<RoleEntity>() {}
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(roleEntitySecond, rs.body)
    }

    @Test
    @Order(6)
    fun editWithWrongId() {
        roleEntitySecond.roleId = roleEntityFirst.roleId
        val rs = restTemplate.exchange(
            url("-1/edit"),
            HttpMethod.PUT,
            HttpEntity(roleEntitySecond, utils.loginAdmin()),
            object : ParameterizedTypeReference<RoleEntity>() {}
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    @Order(7)
    fun deleteWithoutGrants() {
        val rs = restTemplate.exchange(
            url("${roleEntityFirst.roleId}/delete"),
            HttpMethod.DELETE,
            HttpEntity<Nothing>(utils.loginUser()),
            Nothing::class.java
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

    @Test
    @Order(8)
    fun deleteWithGrants() {
        val rs = restTemplate.exchange(
            url("${roleEntityFirst.roleId}/delete"),
            HttpMethod.DELETE,
            HttpEntity<Nothing>(utils.loginAdmin()),
            Nothing::class.java
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
    }

    @Test
    @Order(9)
    fun deleteWithWrongId() {
        val rs = restTemplate.exchange(
            url("-1/delete"),
            HttpMethod.DELETE,
            HttpEntity<Nothing>(utils.loginAdmin()),
            Nothing::class.java
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }
}