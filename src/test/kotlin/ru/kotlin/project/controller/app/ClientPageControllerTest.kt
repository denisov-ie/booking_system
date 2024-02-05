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
import ru.kotlin.project.entity.ClientEntity
import ru.kotlin.project.service.ClientService

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class ClientPageControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var clientService: ClientService
    private lateinit var clientEntityFirst: ClientEntity
    private lateinit var clientEntitySecond: ClientEntity

    @Autowired
    private lateinit var utils: Utils

    @BeforeAll
    fun setUp() {
        utils.createUsers()
        clientEntityFirst = ClientEntity(
            clientId = 0,
            name = "ClientServiceTest First",
            phone = "89111234567"
        )
        clientService.add(clientEntityFirst)
        clientEntitySecond = ClientEntity(
            clientId = 1,
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
    @WithMockUser(username = "ADMIN", authorities = ["ADMIN"])
    fun add() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/app/client/add")
                .flashAttr("entity", clientEntitySecond)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
            .andExpect(MockMvcResultMatchers.view().name("redirect:/app/client/list"))
    }

    @Test
    @Order(2)
    @WithMockUser(username = "ADMIN", authorities = ["ADMIN"])
    fun list() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/app/client/list")
                .flashAttr("entity", clientEntityFirst)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("client/list"))
    }

    @Test
    @Order(3)
    @WithMockUser(username = "ADMIN", authorities = ["ADMIN"])
    fun get() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/app/client/{clientId}/get", clientEntityFirst.clientId)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("client/get"))
    }

    @Test
    @Order(4)
    @WithMockUser(username = "ADMIN", authorities = ["ADMIN"])
    fun edit() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/app/client/{clientId}/edit", clientEntityFirst.clientId)
                .flashAttr("entity", clientEntitySecond)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
            .andExpect(MockMvcResultMatchers.view().name("redirect:/app/client/list"))
    }

    @Test
    @Order(5)
    @WithMockUser(username = "MANAGER", authorities = ["USER"])
    fun deleteWithoutGrants() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/app/client/{clientId}/delete", clientEntityFirst.clientId)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @Test
    @Order(6)
    @WithMockUser(username = "ADMIN", authorities = ["ADMIN"])
    fun deleteWithGrants() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/app/client/{clientId}/delete", clientEntityFirst.clientId)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
            .andExpect(MockMvcResultMatchers.view().name("redirect:/app/client/list"))
    }
}