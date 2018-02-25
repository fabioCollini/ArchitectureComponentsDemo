package it.codingjam.github.core

data class RepoDetail(
        val repo: Repo,
        val contributors: List<Contributor>
)