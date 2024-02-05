package ru.kotlin.project.controller.api

import org.junit.jupiter.api.Assertions.*
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
import ru.kotlin.project.dto.BookingParametersDto
import ru.kotlin.project.dto.TimeParametersDto
import ru.kotlin.project.Utils
import ru.kotlin.project.entity.*
import ru.kotlin.project.service.*
import java.sql.Date
import java.sql.Time

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class BookingControllerTest {
    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var clientService: ClientService
    private lateinit var clientEntity: ClientEntity
    private var clientId: Long? = null

    @Autowired
    private lateinit var timeSlotService: TimeSlotService
    private lateinit var timeSlotParameters: TimeParametersDto
    private var timeSlotIds: ArrayList<Long> = ArrayList()

    @Autowired
    private lateinit var operationService: OperationService
    private lateinit var operationEntity: OperationEntity
    private var operationId: Long? = null

    @Autowired
    private lateinit var utils: Utils

    private lateinit var bookingParameters: BookingParametersDto

    private fun url(path: String) = "http://localhost:${port}/api/booking/${path}"

    @BeforeAll
    fun setUp() {
        utils.createUsers()
        operationEntity = OperationEntity(
            title = "OperationServiceTest First",
            description = "OperationServiceTest First Description",
            duration = 120
        )
        operationId = operationService.add(operationEntity).body?.operationId ?: 0
        timeSlotParameters = TimeParametersDto(
            dateFor = Date.valueOf("2025-01-12"),
            timeFrom = Time.valueOf("08:30:00"),
            timeTo = Time.valueOf("17:30:00"),
            operationEntity = operationEntity
        )
        clientEntity = ClientEntity(
            name = "ClientServiceTest First",
            phone = "89111234567"
        )
        clientId = clientService.add(clientEntity).body?.clientId ?: 0
    }

    @AfterAll
    fun tearDown() {
        operationService.delete(operationId)
        timeSlotIds.stream().forEach { id -> timeSlotService.delete(id) }
        clientService.delete(clientId)
        utils.dropUsers()
    }

    @Test
    @Order(1)
    fun open() {
        val rs = restTemplate.exchange(
            url("${operationId}/open"),
            HttpMethod.PUT,
            HttpEntity(timeSlotParameters, utils.loginUser()),
            object: ParameterizedTypeReference<List<TimeSlotEntity>>() {}
        )
        assertEquals(HttpStatus.CREATED, rs.statusCode)
        rs.body?.stream()?.forEach { entity -> timeSlotIds.add(entity.timeSlotId) }
        rs.body?.stream()?.forEach { entity -> assertEquals(entity.operationEntity, operationEntity) }
        assertEquals(rs.body?.size, 4)
    }

    @Test
    @Order(2)
    fun add() {
        bookingParameters = BookingParametersDto(
            clientId = clientId!!,
            operationId = operationId!!,
            timeSlotId = timeSlotIds[0]
        )
        val rs = restTemplate.exchange(
            url("add"),
            HttpMethod.POST,
            HttpEntity(bookingParameters, utils.loginUser()),
            object: ParameterizedTypeReference<TimeSlotEntity>() {}
        )
        assertEquals(HttpStatus.OK, rs.statusCode)
        assertEquals(rs.body?.clientEntity, clientEntity)
    }

    @Test
    @Order(3)
    fun available() {
        val rs = restTemplate.exchange(
            url("${operationId}/available"),
            HttpMethod.GET,
            HttpEntity<Nothing>(utils.loginUser()),
            object: ParameterizedTypeReference<List<TimeSlotEntity>>() {}
        )
        assertEquals(HttpStatus.OK, rs.statusCode)
        rs.body?.stream()?.forEach { entity -> assertEquals(entity.operationEntity, operationEntity) }
        rs.body?.stream()?.forEach { entity -> assertEquals(entity.clientEntity, null) }
        assertEquals(rs.body?.size, 3)
    }

    @Test
    @Order(4)
    fun booked() {
        val rs = restTemplate.exchange(
            url("${operationId}/booked"),
            HttpMethod.GET,
            HttpEntity<Nothing>(utils.loginUser()),
            object: ParameterizedTypeReference<List<TimeSlotEntity>>() {}
        )
        assertEquals(HttpStatus.OK, rs.statusCode)
        rs.body?.stream()?.forEach { entity -> assertEquals(entity.operationEntity, operationEntity) }
        rs.body?.stream()?.forEach { entity -> assertEquals(entity.clientEntity, clientEntity) }
        assertEquals(rs.body?.size, 1)
    }

    @Test
    @Order(5)
    fun active() {
        val rs = restTemplate.exchange(
            url("${clientId}/active"),
            HttpMethod.GET,
            HttpEntity<Nothing>(utils.loginUser()),
            object: ParameterizedTypeReference<List<TimeSlotEntity>>() {}
        )
        assertEquals(HttpStatus.OK, rs.statusCode)
        rs.body?.stream()?.forEach { entity -> assertEquals(entity.operationEntity, operationEntity) }
        rs.body?.stream()?.forEach { entity -> assertEquals(entity.clientEntity, clientEntity) }
        assertEquals(rs.body?.size, 1)
    }

    @Test
    @Order(6)
    fun remove() {
        val rs = restTemplate.exchange(
            url("${timeSlotIds[0]}/remove"),
            HttpMethod.GET,
            HttpEntity<Nothing>(utils.loginUser()),
            object: ParameterizedTypeReference<TimeSlotEntity>() {}
        )
        assertEquals(HttpStatus.OK, rs.statusCode)
        assertEquals(rs.body?.clientEntity, null)
    }
}