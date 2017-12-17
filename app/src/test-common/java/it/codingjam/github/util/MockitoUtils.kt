package it.codingjam.github.util

import com.nhaarman.mockito_kotlin.given
import org.mockito.BDDMockito

infix fun <T> T?.willReturn(value: T): BDDMockito.BDDMyOngoingStubbing<T?> =
        given(this).willReturn(value)

infix fun <T> T?.willThrow(throwable: Throwable): BDDMockito.BDDMyOngoingStubbing<T?> =
        given(this).willThrow(throwable)
