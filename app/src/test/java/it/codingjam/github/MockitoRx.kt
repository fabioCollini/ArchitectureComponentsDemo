package it.codingjam.github

import io.reactivex.Single
import org.mockito.BDDMockito

infix fun <T> BDDMockito.BDDMyOngoingStubbing<Single<T>>.willReturnSingle(value: () -> T): BDDMockito.BDDMyOngoingStubbing<Single<T>> =
        willReturn(Single.fromCallable(value))

infix fun <T> BDDMockito.BDDMyOngoingStubbing<Single<T>>.willReturnJust(value: T): BDDMockito.BDDMyOngoingStubbing<Single<T>> =
        willReturn(Single.just(value))

infix fun <T> BDDMockito.BDDMyOngoingStubbing<Single<T>>.willThrowSingle(value: () -> Throwable): BDDMockito.BDDMyOngoingStubbing<Single<T>> =
        willReturn(Single.error(value))