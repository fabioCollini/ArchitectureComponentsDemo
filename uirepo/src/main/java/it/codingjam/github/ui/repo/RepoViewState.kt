package it.codingjam.github.ui.repo

import it.codingjam.github.core.RepoDetail
import it.codingjam.github.vo.Lce

data class RepoViewState(val repoDetail: Lce<RepoDetail>)