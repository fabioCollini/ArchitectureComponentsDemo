package it.codingjam.github.vo

data class RepoDetail(
        val repo: Repo,
        val contributors: List<Contributor>
)