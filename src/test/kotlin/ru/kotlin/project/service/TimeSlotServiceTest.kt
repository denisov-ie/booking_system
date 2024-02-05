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
import ru.kotlin.project.Utils
import ru.kotlin.project.dto.TimeParametersDto
import ru.kotlin.project.entity.OperationEntity
import ru.kotlin.project.entity.TimeSlotEntity
import java.sql.Date
import java.sql.Time

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class TimeSlotServiceTest {
    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var operationService: OperationService
    private lateinit var operationEntity: OperationEntity
    private var operationId: Long? = null

    @Autowired
    private lateinit var timeSlotService: TimeSlotService
    private lateinit var timeSlotEntityFirst: TimeSlotEntity
    private lateinit var timeSlotEntitySecond: TimeSlotEntity
    private lateinit var timeSlotParameters: TimeParametersDto

    @Autowired
    private lateinit var utils: Utils

    private fun url(path: String) = "http://localhost:${port}/api/timeslot/${path}"

    @BeforeAll
    fun setUp() {
        utils.createUsers()
        operationEntity = OperationEntity(
            title = "OperationServiceTest First",
            description = "OperationServiceTest First Description",
            duration = 120
        )
        operationId = operationService.add(operationEntity).body?.operationId ?: 0
        timeSlotEntityFirst = TimeSlotEntity(
            dateFor = Date.valueOf("2025-01-12"),
            timeFrom = Time.valueOf("06:30:00"),
            timeTo = Time.valueOf("07:30:00"),
            operationEntity = operationEntity
        )
        timeSlotEntitySecond = TimeSlotEntity(
            dateFor = Date.valueOf("2025-01-12"),
            timeFrom = Time.valueOf("07:30:00"),
            timeTo = Time.valueOf("08:30:00"),
            operationEntity = operationEntity
        )
        timeSlotParameters = TimeParametersDto(
            dateFor = Date.valueOf("2025-01-12"),
            timeFrom = Time.valueOf("06:30:00"),
            timeTo = Time.valueOf("07:30:00"),
            operationEntity = operationEntity
        )
    }

    @AfterAll
    fun tearDown() {
        timeSlotService.delete(timeSlotEntityFirst.timeSlotId)
        operationService.delete(operationId)
        utils.dropUsers()
    }

    @Test
    @Order(1)
    fun add() {
        val rs = restTemplate.exchange(
            url("${operationId}/add"),
            HttpMethod.PUT,
            HttpEntity(timeSlotParameters, utils.loginUser()),
            object: ParameterizedTypeReference<TimeSlotEntity>() {}
        )
        timeSlotEntityFirst.timeSlotId = rs.body!!.timeSlotId
        assertEquals(HttpStatus.CREATED, rs.statusCode)
        assertEquals(timeSlotEntityFirst.timeSlotId, rs.body?.timeSlotId)
        assertEquals(timeSlotEntityFirst.timeTo, rs.body?.timeTo)
        assertEquals(timeSlotEntityFirst.timeFrom, rs.body?.timeFrom)
    }

    @Test
    @Order(2)
    fun list() {
        val rs = restTemplate.exchange(
            url("list"),
            HttpMethod.GET,
            HttpEntity<Nothing>(utils.loginUser()),
            object: ParameterizedTypeReference<List<TimeSlotEntity>>() {}
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.OK)
        assertTrue(rs.body!!.stream().anyMatch { timeSlot -> timeSlot.timeSlotId == timeSlotEntityFirst.timeSlotId })
    }

    @Test
    @Order(3)
    fun get() {
        val rs = restTemplate.exchange(
            url("${timeSlotEntityFirst.timeSlotId}/get"),
            HttpMethod.GET,
            HttpEntity<Nothing>(utils.loginUser()),
            object: ParameterizedTypeReference<TimeSlotEntity>() {}
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(timeSlotEntityFirst.timeSlotId, rs.body?.timeSlotId)
        assertEquals(timeSlotEntityFirst.timeTo, rs.body?.timeTo)
        assertEquals(timeSlotEntityFirst.timeFrom, rs.body?.timeFrom)
    }

    @Test
    @Order(4)
    fun getWithWrongId() {
        val rs = restTemplate.exchange(
            url("-1/get"),
            HttpMethod.GET,
            HttpEntity<Nothing>(utils.loginUser()),
            object: ParameterizedTypeReference<TimeSlotEntity>() {}
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    @Order(5)
    fun edit() {
        timeSlotEntitySecond.timeSlotId = timeSlotEntityFirst.timeSlotId
        val rs = restTemplate.exchange(
            url("${timeSlotEntityFirst.timeSlotId}/edit"),
            HttpMethod.PUT,
            HttpEntity(timeSlotEntitySecond, utils.loginUser()),
            object: ParameterizedTypeReference<TimeSlotEntity>() {}
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(timeSlotEntitySecond.timeSlotId, rs.body?.timeSlotId)
        assertEquals(timeSlotEntitySecond.timeTo, rs.body?.timeTo)
        assertEquals(timeSlotEntitySecond.timeFrom, rs.body?.timeFrom)
    }

    @Test
    @Order(6)
    fun editWithWrongId() {
        timeSlotEntitySecond.timeSlotId = timeSlotEntityFirst.timeSlotId
        val rs = restTemplate.exchange(
            url("-1/edit"),
            HttpMethod.PUT,
            HttpEntity(timeSlotEntitySecond, utils.loginUser()),
            object: ParameterizedTypeReference<TimeSlotEntity>() {}
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    @Order(7)
    fun deleteWithoutGrants() {
        val rs = restTemplate.exchange(
            url("${timeSlotEntityFirst.timeSlotId}/delete"),
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
            url("${timeSlotEntityFirst.timeSlotId}/delete"),
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