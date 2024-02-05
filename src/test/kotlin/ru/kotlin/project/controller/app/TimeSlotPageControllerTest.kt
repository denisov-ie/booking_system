package ru.kotlin.project.controller.app

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import ru.kotlin.project.Utils
import ru.kotlin.project.dto.TimeParametersDto
import ru.kotlin.project.entity.OperationEntity
import ru.kotlin.project.entity.TimeSlotEntity
import ru.kotlin.project.service.OperationService
import ru.kotlin.project.service.TimeSlotService
import java.sql.Date
import java.sql.Time

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class TimeSlotPageControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var operationService: OperationService
    private lateinit var operationEntity: OperationEntity
    private var operationId: Long? = null

    @Autowired
    private lateinit var timeSlotService: TimeSlotService
    private lateinit var timeSlotEntityFirst: TimeSlotEntity
    private lateinit var timeSlotEntitySecond: TimeSlotEntity
    private lateinit var timeSlotParameters: TimeParametersDto
    private var timeSlotId: Long? = null

    @Autowired
    private lateinit var utils: Utils

    @BeforeAll
    fun setUp() {
        utils.createUsers()
        operationEntity = OperationEntity(
            title = "OperationServiceTest First",
            description = "OperationServiceTest First Description",
            duration = 120
        )
        operationId = operationService.add(operationEntity).body?.operationId ?: 0
        operationEntity.operationId = operationId ?: 0
        timeSlotEntityFirst = TimeSlotEntity(
            timeSlotId = 0,
            dateFor = Date.valueOf("2025-01-12"),
            timeFrom = Time.valueOf("06:30:00"),
            timeTo = Time.valueOf("07:30:00"),
            operationEntity = operationEntity
        )
        timeSlotEntitySecond = TimeSlotEntity(
            timeSlotId = 1,
            dateFor = Date.valueOf("2025-01-12"),
            timeFrom = Time.valueOf("07:30:00"),
            timeTo = Time.valueOf("08:30:00"),
            operationEntity = operationEntity
        )
        timeSlotParameters = TimeParametersDto(
            dateFor = Date.valueOf("2025-01-12"),
            timeFrom = Time.valueOf("06:30:00"),
            timeTo = Time.valueOf("07:30:00"),
            isLocked = false,
            operationEntity = operationEntity
        )
        timeSlotId = (timeSlotService.add(timeSlotParameters, false).body as TimeSlotEntity).timeSlotId
        timeSlotParameters.timeSlotId = timeSlotId
    }

    @AfterAll
    fun tearDown() {
        timeSlotService.delete(timeSlotEntityFirst.timeSlotId)
        operationService.delete(operationId)
        utils.dropUsers()
    }

    @Test
    @Order(1)
    @WithMockUser(username = "ADMIN", authorities = ["ADMIN"])
    fun add() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/app/timeslot/add")
                .flashAttr("entity", timeSlotEntityFirst)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
            .andExpect(MockMvcResultMatchers.view().name("redirect:/app/timeslot/list"))
    }

    @Test
    @Order(2)
    @WithMockUser(username = "ADMIN", authorities = ["ADMIN"])
    fun list() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/app/timeslot/list")
                .flashAttr("entity", timeSlotEntityFirst)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("timeslot/list"))
    }

    @Test
    @Order(3)
    @WithMockUser(username = "ADMIN", authorities = ["ADMIN"])
    fun get() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/app/timeslot/{timeslotId}/get", timeSlotId)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("timeslot/get"))
    }

    @Test
    @Order(4)
    @WithMockUser(username = "MANAGER", authorities = ["USER"])
    fun deleteWithoutGrants() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/app/timeslot/{timeslotId}/delete", timeSlotEntityFirst.timeSlotId)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @Test
    @Order(5)
    @WithMockUser(username = "ADMIN", authorities = ["ADMIN"])
    fun deleteWithGrants() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/app/timeslot/{timeslotId}/delete", timeSlotEntityFirst.timeSlotId)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
            .andExpect(MockMvcResultMatchers.view().name("redirect:/app/timeslot/list"))
    }
}