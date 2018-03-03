package it.codingjam.github.espresso

import android.app.Application
import android.content.Context
import com.github.tmurakami.dexopener.DexOpenerAndroidJUnitRunner

class MockTestRunner: DexOpenerAndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, TestApplication::class.java.name, context)
    }
}