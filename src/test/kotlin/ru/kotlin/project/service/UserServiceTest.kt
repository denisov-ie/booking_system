package ru.kotlin.project.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import ru.kotlin.project.ExtendedUserEntity
import ru.kotlin.project.Utils
import ru.kotlin.project.dto.UserParametersDto
import ru.kotlin.project.entity.UserEntity

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class UserServiceTest {
    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var userService: UserService
    private lateinit var userEntityFirst: UserEntity
    private lateinit var userEntitySecond: UserEntity
    private lateinit var userParameters: UserParametersDto

    @Autowired
    private lateinit var utils: Utils

    private fun url(path: String) = "http://localhost:${port}/api/user/${path}"

    @BeforeAll
    fun setUp() {
        utils.createUsers()
        userEntityFirst = UserEntity(
            login = "UserServiceTest_First",
            pass = "UserServiceTest_First_Pass"
        )
        userEntitySecond = UserEntity(
            login = "UserServiceTest_Second",
            pass = "UserServiceTest_Second_Pass"
        )
        userParameters = UserParametersDto(
            login = "UserServiceTest_First",
            pass = "UserServiceTest_First_Pass"
        )
    }

    @AfterAll
    fun tearDown() {
        userService.delete(userEntityFirst.userId)
        utils.dropUsers()
    }

    @Test
    @Order(1)
    fun add() {
        val rs = restTemplate.exchange(
            url("add"),
            HttpMethod.POST,
            HttpEntity(userParameters, utils.loginAdmin()),
            object: ParameterizedTypeReference<UserEntity>() {}
        )
        userEntityFirst.userId = rs.body!!.userId
        assertEquals(HttpStatus.CREATED, rs.statusCode)
        assertEquals(userEntityFirst.userId, rs.body?.userId)
        assertEquals(userEntityFirst.login, rs.body?.login)
    }

    @Test
    @Order(2)
    fun list() {
        val rs = restTemplate.exchange(
            url("list"),
            HttpMethod.GET,
            HttpEntity<Nothing>(utils.loginAdmin()),
            object: ParameterizedTypeReference<List<ExtendedUserEntity>>() {}
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.OK)
        assertTrue(rs.body!!.stream().anyMatch { operation -> operation.userId == userEntityFirst.userId })
    }

    @Test
    @Order(3)
    fun get() {
        val rs = restTemplate.exchange(
            url("${userEntityFirst.userId}/get"),
            HttpMethod.GET,
            HttpEntity<Nothing>(utils.loginAdmin()),
            object: ParameterizedTypeReference<ExtendedUserEntity>() {}
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(userEntityFirst.userId, rs.body?.userId)
        assertEquals(userEntityFirst.login, rs.body?.login)
    }

    @Test
    @Order(4)
    fun getWithWrongId() {
        val rs = restTemplate.exchange(
            url("-1/get"),
            HttpMethod.GET,
            HttpEntity<Nothing>(utils.loginAdmin()),
            object: ParameterizedTypeReference<ExtendedUserEntity>() {}
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    @Order(5)
    fun edit() {
        userEntitySecond.userId = userEntityFirst.userId
        val rs = restTemplate.exchange(
            url("${userEntityFirst.userId}/edit"),
            HttpMethod.PUT,
            HttpEntity(userEntitySecond, utils.loginAdmin()),
            object: ParameterizedTypeReference<UserEntity>() {}
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(userEntitySecond.userId, rs.body?.userId)
        assertEquals(userEntitySecond.login, rs.body?.login)
    }

    @Test
    @Order(6)
    fun editWithWrongId() {
        userEntitySecond.userId = userEntityFirst.userId
        val rs = restTemplate.exchange(
            url("-1/edit"),
            HttpMethod.PUT,
            HttpEntity(userEntitySecond, utils.loginAdmin()),
            object: ParameterizedTypeReference<UserEntity>() {}
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    @Order(7)
    fun deleteWithoutGrants() {
        val rs = restTemplate.exchange(
            url("${userEntityFirst.userId}/delete"),
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
            url("${userEntityFirst.userId}/delete"),
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
