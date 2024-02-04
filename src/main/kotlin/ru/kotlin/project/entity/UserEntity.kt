package ru.kotlin.project.entity

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*

@Entity
@Table(name = "users")
data class UserEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    var userId: Long = 0,

    var login: String,

    var pass: String,

    @ManyToOne(cascade = [CascadeType.MERGE], fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    var roleEntity: RoleEntity? = null
): UserDetails

{
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val authorities: MutableSet<GrantedAuthority> = HashSet()
        if (roleEntity != null) {
            authorities.add(SimpleGrantedAuthority(roleEntity!!.role))
        }
        return authorities
    }

    override fun getPassword(): String {
        return pass
    }

    override fun getUsername(): String {
        return login
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun toString(): String {
        return "User(userId=$userId, login=$login, pass=$pass)"
    }
}