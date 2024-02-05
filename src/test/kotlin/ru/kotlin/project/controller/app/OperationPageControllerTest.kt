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
import ru.kotlin.project.entity.OperationEntity
import ru.kotlin.project.service.OperationService

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class OperationPageControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var operationService: OperationService
    private lateinit var operationEntityFirst: OperationEntity
    private lateinit var operationEntitySecond: OperationEntity

    @Autowired
    private lateinit var utils: Utils

    @BeforeAll
    fun setUp() {
        utils.createUsers()
        operationEntityFirst = OperationEntity(
            operationId = 0,
            title = "OperationServiceTest First",
            description = "OperationServiceTest First Description",
            duration = 120
        )
        operationService.add(operationEntityFirst)
        operationEntitySecond = OperationEntity(
            operationId = 1,
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
    @WithMockUser(username = "ADMIN", authorities = ["ADMIN"])
    fun add() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/app/operation/add")
                .flashAttr("entity", operationEntitySecond)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
            .andExpect(MockMvcResultMatchers.view().name("redirect:/app/operation/list"))
    }

    @Test
    @Order(2)
    @WithMockUser(username = "ADMIN", authorities = ["ADMIN"])
    fun list() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/app/operation/list")
                .flashAttr("entity", operationEntityFirst)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("operation/list"))
    }

    @Test
    @Order(3)
    @WithMockUser(username = "ADMIN", authorities = ["ADMIN"])
    fun get() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/app/operation/{operationId}/get", operationEntityFirst.operationId)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("operation/get"))
    }

    @Test
    @Order(4)
    @WithMockUser(username = "ADMIN", authorities = ["ADMIN"])
    fun edit() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/app/operation/{operationId}/edit", operationEntityFirst.operationId)
                .flashAttr("entity", operationEntitySecond)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
            .andExpect(MockMvcResultMatchers.view().name("redirect:/app/operation/list"))
    }

    @Test
    @Order(5)
    @WithMockUser(username = "MANAGER", authorities = ["USER"])
    fun deleteWithoutGrants() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/app/operation/{operationId}/delete", operationEntityFirst.operationId)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @Test
    @Order(6)
    @WithMockUser(username = "ADMIN", authorities = ["ADMIN"])
    fun deleteWithGrants() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/app/operation/{operationId}/delete", operationEntityFirst.operationId)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
            .andExpect(MockMvcResultMatchers.view().name("redirect:/app/operation/list"))
    }
}