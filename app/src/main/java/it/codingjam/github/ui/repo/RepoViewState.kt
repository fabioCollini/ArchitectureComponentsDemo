package it.codingjam.github.ui.repo

import it.codingjam.github.vo.*

data class RepoViewState(val repoDetail: Resource<RepoDetail>) {
    fun repo(): Repo? = repoDetail.map { it.repo }.orElse(null)

    fun contributors(): List<Contributor> = repoDetail.map { it.contributors }.orElse(emptyList())
}
