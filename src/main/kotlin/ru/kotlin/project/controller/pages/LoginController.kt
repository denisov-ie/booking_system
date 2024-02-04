package ru.kotlin.project.controller.pages

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class LoginController {

    @GetMapping("/login")
    operator fun get(model: Model): String? {
        return "login"
    }
}