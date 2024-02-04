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
import ru.kotlin.project.entity.ClientEntity

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class ClientServiceTest {
    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var clientService: ClientService
    private lateinit var clientEntityFirst: ClientEntity
    private lateinit var clientEntitySecond: ClientEntity

    @Autowired
    private lateinit var utils: Utils

    private fun url(path: String) = "http://localhost:${port}/api/client/${path}"

    @BeforeAll
    fun setUp() {
        utils.createUsers()
        clientEntityFirst = ClientEntity(
            name = "ClientServiceTest First",
            phone = "89111234567"
        )
        clientEntitySecond = ClientEntity(
            name = "ClientServiceTest Second",
            phone = "89211234567"
        )
    }

    @AfterAll
    fun tearDown() {
        clientService.delete(clientEntityFirst.clientId)
        utils.dropUsers()
    }

    @Test
    @Order(1)
    fun add() {
        val rs = restTemplate.exchange(
            url("add"),
            HttpMethod.POST,
            HttpEntity(clientEntityFirst, utils.loginUser()),
            object: ParameterizedTypeReference<ClientEntity>() {}
        )
        clientEntityFirst.clientId = rs.body!!.clientId
        assertEquals(HttpStatus.CREATED, rs.statusCode)
        assertEquals(clientEntityFirst, rs.body)
    }

    @Test
    @Order(2)
    fun list() {
        val rs = restTemplate.exchange(
            url("list"),
            HttpMethod.GET,
            HttpEntity<Nothing>(utils.loginUser()),
            object: ParameterizedTypeReference<List<ClientEntity>>() {}
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.OK)
        assertTrue(rs.body!!.stream().anyMatch { client -> client.clientId == clientEntityFirst.clientId })
    }

    @Test
    @Order(3)
    fun get() {
        val rs = restTemplate.exchange(
            url("${clientEntityFirst.clientId}/get"),
            HttpMethod.GET,
            HttpEntity<Nothing>(utils.loginUser()),
            object: ParameterizedTypeReference<ClientEntity>() {}
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(clientEntityFirst, rs.body)
    }

    @Test
    @Order(4)
    fun getWithWrongId() {
        val rs = restTemplate.exchange(
            url("-1/get"),
            HttpMethod.GET,
            HttpEntity<Nothing>(utils.loginUser()),
            object: ParameterizedTypeReference<ClientEntity>() {}
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    @Order(5)
    fun edit() {
        clientEntitySecond.clientId = clientEntityFirst.clientId
        val rs = restTemplate.exchange(
            url("${clientEntityFirst.clientId}/edit"),
            HttpMethod.PUT,
            HttpEntity(clientEntitySecond, utils.loginUser()),
            object: ParameterizedTypeReference<ClientEntity>() {}
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(clientEntitySecond, rs.body)
    }

    @Test
    @Order(6)
    fun editWithWrongId() {
        clientEntitySecond.clientId = clientEntityFirst.clientId
        val rs = restTemplate.exchange(
            url("-1/edit"),
            HttpMethod.PUT,
            HttpEntity(clientEntitySecond, utils.loginUser()),
            object: ParameterizedTypeReference<ClientEntity>() {}
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    @Order(7)
    fun deleteWithoutGrants() {
        val rs = restTemplate.exchange(
            url("${clientEntityFirst.clientId}/delete"),
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
            url("${clientEntityFirst.clientId}/delete"),
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