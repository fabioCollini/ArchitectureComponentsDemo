package it.codingjam.github.ui.user

import it.codingjam.github.core.Repo
import it.codingjam.github.core.User
import it.codingjam.github.core.UserDetail
import it.codingjam.github.vo.Lce
import it.codingjam.github.vo.orElse

data class UserViewState(val userDetail: Lce<UserDetail>) {
    fun repos(): List<Repo> = userDetail.map { it.repos }.orElse(emptyList())

    fun user(): User? = userDetail.map { it.user }.orElse(null)
}
