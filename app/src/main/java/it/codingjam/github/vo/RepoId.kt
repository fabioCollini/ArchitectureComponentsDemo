package it.codingjam.github.vo

import io.mironov.smuggler.AutoParcelable

data class RepoId(val owner: String, val name: String) : AutoParcelable