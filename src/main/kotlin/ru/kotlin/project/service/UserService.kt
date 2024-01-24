package ru.kotlin.project.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import ru.kotlin.project.repository.UserRepository

@Service
class UserService @Autowired constructor(
    private val userRepository: UserRepository
): UserDetailsService

{
    override fun loadUserByUsername(username: String?): UserDetails {
        return userRepository.findByLogin(username) ?: throw UsernameNotFoundException(username)
    }
}