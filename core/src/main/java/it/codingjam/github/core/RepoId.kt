package it.codingjam.github.core

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RepoId(val owner: String, val name: String) : Parcelable