package it.codingjam.github.ui.user

import it.codingjam.github.vo.Repo
import it.codingjam.github.vo.User

data class UserDetail (
    val user: User,
    val repos: List<Repo>
)