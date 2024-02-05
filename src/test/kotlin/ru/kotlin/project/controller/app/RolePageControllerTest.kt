package ru.kotlin.project.controller.app

import org.junit.jupiter.api.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ru.kotlin.project.Utils
import ru.kotlin.project.entity.RoleEntity
import ru.kotlin.project.service.RoleService

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class RolePageControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var roleService: RoleService
    private lateinit var roleEntityFirst: RoleEntity
    private lateinit var roleEntitySecond: RoleEntity

    @Autowired
    private lateinit var utils: Utils

    @BeforeAll
    fun setUp() {
        utils.createUsers()
        roleEntityFirst = RoleEntity(
            roleId = 0,
            role = "TEST_USER"
        )
        roleService.add(roleEntityFirst)
        roleEntitySecond = RoleEntity(
            roleId = 1,
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
    @WithMockUser(username = "ADMIN", authorities = ["ADMIN"])
    fun add() {
        mockMvc.perform(
            post("/app/role/add")
                .flashAttr("entity", roleEntitySecond)
        )
            .andDo(print())
            .andExpect(status().is3xxRedirection)
            .andExpect(MockMvcResultMatchers.view().name("redirect:/app/role/list"))
    }

    @Test
    @Order(2)
    @WithMockUser(username = "ADMIN", authorities = ["ADMIN"])
    fun list() {
        mockMvc.perform(
            get("/app/role/list")
                .flashAttr("entity", roleEntityFirst)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("role/list"))
    }

    @Test
    @Order(3)
    @WithMockUser(username = "ADMIN", authorities = ["ADMIN"])
    fun get() {
        mockMvc.perform(
            get("/app/role/{roleId}/get", roleEntityFirst.roleId)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("role/get"))
    }

    @Test
    @Order(4)
    @WithMockUser(username = "ADMIN", authorities = ["ADMIN"])
    fun edit() {
        mockMvc.perform(
            post("/app/role/{roleId}/edit", roleEntityFirst.roleId)
                .flashAttr("entity", roleEntitySecond)
        )
            .andDo(print())
            .andExpect(status().is3xxRedirection)
            .andExpect(MockMvcResultMatchers.view().name("redirect:/app/role/list"))
    }

    @Test
    @Order(5)
    @WithMockUser(username = "MANAGER", authorities = ["USER"])
    fun deleteWithoutGrants() {
        mockMvc.perform(
            get("/app/role/{roleId}/delete", roleEntityFirst.roleId)
        )
            .andDo(print())
            .andExpect(status().isForbidden)
    }

    @Test
    @Order(6)
    @WithMockUser(username = "ADMIN", authorities = ["ADMIN"])
    fun deleteWithGrants() {
        mockMvc.perform(
            get("/app/role/{roleId}/delete", roleEntityFirst.roleId)
        )
            .andDo(print())
            .andExpect(status().is3xxRedirection)
            .andExpect(MockMvcResultMatchers.view().name("redirect:/app/role/list"))
    }
}