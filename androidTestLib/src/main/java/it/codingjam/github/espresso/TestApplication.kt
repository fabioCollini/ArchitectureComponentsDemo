package it.codingjam.github.espresso

import android.app.Application
import it.codingjam.github.core.utils.ComponentHolder
import it.codingjam.github.core.utils.TestComponentsMap

class TestApplication : Application(), ComponentHolder by TestComponentsMap()