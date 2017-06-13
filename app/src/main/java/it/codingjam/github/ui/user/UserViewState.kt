package it.codingjam.github.ui.user

import it.codingjam.github.vo.Repo
import it.codingjam.github.vo.Resource
import it.codingjam.github.vo.User
import it.codingjam.github.vo.orElse

data class UserViewState(val userDetail: Resource<UserDetail>) {
    fun repos(): List<Repo> = userDetail.map { it.repos }.orElse(emptyList())

    fun user(): User? = userDetail.map { it.user }.orElse(null)
}
