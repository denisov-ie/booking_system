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
import ru.kotlin.project.entity.OperationEntity

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class OperationServiceTest {
    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var operationService: OperationService
    private lateinit var operationEntityFirst: OperationEntity
    private lateinit var operationEntitySecond: OperationEntity

    @Autowired
    private lateinit var utils: Utils

    private fun url(path: String) = "http://localhost:${port}/api/operation/${path}"

    @BeforeAll
    fun setUp() {
        utils.createUsers()
        operationEntityFirst = OperationEntity(
            title = "OperationServiceTest First",
            description = "OperationServiceTest First Description",
            duration = 120
        )
        operationEntitySecond = OperationEntity(
            title = "OperationServiceTest Second",
            description = "OperationServiceTest Second Description",
            duration = 100
        )
    }

    @AfterAll
    fun tearDown() {
        operationService.delete(operationEntityFirst.operationId)
        utils.dropUsers()
    }

    @Test
    @Order(1)
    fun add() {
        val rs = restTemplate.exchange(
            url("add"),
            HttpMethod.POST,
            HttpEntity(operationEntityFirst, utils.loginUser()),
            object: ParameterizedTypeReference<OperationEntity>() {}
        )
        operationEntityFirst.operationId = rs.body!!.operationId
        assertEquals(HttpStatus.CREATED, rs.statusCode)
        assertEquals(operationEntityFirst, rs.body)
    }

    @Test
    @Order(2)
    fun list() {
        val rs = restTemplate.exchange(
            url("list"),
            HttpMethod.GET,
            HttpEntity<Nothing>(utils.loginUser()),
            object: ParameterizedTypeReference<List<OperationEntity>>() {}
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.OK)
        assertTrue(rs.body!!.stream().anyMatch { operation -> operation.operationId == operationEntityFirst.operationId })
    }

    @Test
    @Order(3)
    fun get() {
        val rs = restTemplate.exchange(
            url("${operationEntityFirst.operationId}/get"),
            HttpMethod.GET,
            HttpEntity<Nothing>(utils.loginUser()),
            object: ParameterizedTypeReference<OperationEntity>() {}
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(operationEntityFirst, rs.body)
    }

    @Test
    @Order(4)
    fun getWithWrongId() {
        val rs = restTemplate.exchange(
            url("-1/get"),
            HttpMethod.GET,
            HttpEntity<Nothing>(utils.loginUser()),
            object: ParameterizedTypeReference<OperationEntity>() {}
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    @Order(5)
    fun edit() {
        operationEntitySecond.operationId = operationEntityFirst.operationId
        val rs = restTemplate.exchange(
            url("${operationEntityFirst.operationId}/edit"),
            HttpMethod.PUT,
            HttpEntity(operationEntitySecond, utils.loginUser()),
            object: ParameterizedTypeReference<OperationEntity>() {}
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(operationEntitySecond, rs.body)
    }

    @Test
    @Order(6)
    fun editWithWrongId() {
        operationEntitySecond.operationId = operationEntityFirst.operationId
        val rs = restTemplate.exchange(
            url("-1/edit"),
            HttpMethod.PUT,
            HttpEntity(operationEntitySecond, utils.loginUser()),
            object: ParameterizedTypeReference<OperationEntity>() {}
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    @Order(7)
    fun deleteWithoutGrants() {
        val rs = restTemplate.exchange(
            url("${operationEntityFirst.operationId}/delete"),
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
            url("${operationEntityFirst.operationId}/delete"),
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