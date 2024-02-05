package ru.kotlin.project.controller.app

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class LoginPageController {

    @GetMapping("/login")
    operator fun get(model: Model): String? {
        return "login"
    }
}