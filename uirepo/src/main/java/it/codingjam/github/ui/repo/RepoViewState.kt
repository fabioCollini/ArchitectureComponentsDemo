package it.codingjam.github.ui.repo

import it.codingjam.github.core.Contributor
import it.codingjam.github.core.Repo
import it.codingjam.github.core.RepoDetail
import it.codingjam.github.vo.Resource
import it.codingjam.github.vo.orElse

data class RepoViewState(val repoDetail: Resource<RepoDetail>) {
    fun repo(): Repo? = repoDetail.map { it.repo }.orElse(null)

    fun contributors(): List<Contributor> = repoDetail.map { it.contributors }.orElse(emptyList())
}
