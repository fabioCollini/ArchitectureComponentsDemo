## Android Architecture Components Demo

This is a Kotlin project that uses some Android Architecture Components (ViewModel and LiveData) with Dagger 2 and RxJava.

This project is a fork of official [GithubBrowserSample](https://github.com/googlesamples/android-architecture-components/tree/master/GithubBrowserSample),
I converted it to kotlin and modified some architectural aspects to test ViewModel and LiveData features.

## Main concepts
 * each ViewModel manages an immutable ViewState (implemented using a Kotlin data object)
 * a ViewModel can invoke actions on the ui (for example a SnackBar visualization or a navigation to another activity) 
 using a [UiActionsLiveData](https://github.com/fabioCollini/ArchitectureComponentsDemo/blob/master/app/src/main/java/it/codingjam/github/util/UiActionsLiveData.kt).
 ViewModels don't maintain a reference to the Activity, using the UiActionsLiveData it just schedules action on the ui
 * minimal Dagger configuration
 * ViewModels are instantiated using a [Kotlin delegate](https://github.com/fabioCollini/ArchitectureComponentsDemo/blob/master/app/src/main/java/it/codingjam/github/util/ViewModels.kt)
 * Fragment args are managed using a [companion object base class](https://github.com/fabioCollini/ArchitectureComponentsDemo/blob/master/app/src/main/java/it/codingjam/github/ui/common/FragmentCreator.kt)
 * JVM tests are written using Mockito and [AssertK](https://github.com/willowtreeapps/assertk)
 * Espresso tests are written using [DaggerMock](https://github.com/fabioCollini/DaggerMock) and Mockito
 * asynchronous tasks are managed using [Coroutines](https://github.com/Kotlin/kotlinx.coroutines/blob/master/coroutines-guide.md) (the [rxjava branch](https://github.com/fabioCollini/ArchitectureComponentsDemo/tree/rxjava) contains the same example with RxJava singles instead of coroutines)
