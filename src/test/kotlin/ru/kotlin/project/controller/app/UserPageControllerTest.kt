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
import ru.kotlin.project.dto.UserParametersDto
import ru.kotlin.project.entity.UserEntity
import ru.kotlin.project.service.UserService

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class UserPageControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userService: UserService
    private lateinit var userEntityFirst: UserEntity
    private lateinit var userEntitySecond: UserEntity
    private lateinit var userParameters: UserParametersDto

    @Autowired
    private lateinit var utils: Utils

    @BeforeAll
    fun setUp() {
        utils.createUsers()
        userEntityFirst = UserEntity(
            userId = 0,
            login = "UserServiceTest_First",
            pass = "UserServiceTest_First_Pass"
        )
        userService.add(userEntityFirst)
        userEntitySecond = UserEntity(
            userId = 1,
            login = "UserServiceTest_Second",
            pass = "UserServiceTest_Second_Pass"
        )
        userParameters = UserParametersDto(
            userId = 0,
            roleId = 0,
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
    @WithMockUser(username = "ADMIN", authorities = ["ADMIN"])
    fun add() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/app/user/add")
                .flashAttr("entity", userParameters)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
            .andExpect(MockMvcResultMatchers.view().name("redirect:/app/user/list"))
    }

    @Test
    @Order(2)
    @WithMockUser(username = "ADMIN", authorities = ["ADMIN"])
    fun list() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/app/user/list")
                .flashAttr("entity", userEntityFirst)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("user/list"))
    }

    @Test
    @Order(3)
    @WithMockUser(username = "ADMIN", authorities = ["ADMIN"])
    fun get() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/app/user/{userId}/get", userEntityFirst.userId)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("user/get"))
    }

    @Test
    @Order(4)
    @WithMockUser(username = "ADMIN", authorities = ["ADMIN"])
    fun edit() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/app/user/{userId}/edit", userEntityFirst.userId)
                .flashAttr("entity", userEntitySecond)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
            .andExpect(MockMvcResultMatchers.view().name("redirect:/app/user/list"))
    }

    @Test
    @Order(5)
    @WithMockUser(username = "MANAGER", authorities = ["USER"])
    fun deleteWithoutGrants() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/app/user/{userId}/delete", userEntityFirst.userId)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @Test
    @Order(6)
    @WithMockUser(username = "ADMIN", authorities = ["ADMIN"])
    fun deleteWithGrants() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/app/user/{userId}/delete", userEntityFirst.userId)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
            .andExpect(MockMvcResultMatchers.view().name("redirect:/app/user/list"))
    }
}