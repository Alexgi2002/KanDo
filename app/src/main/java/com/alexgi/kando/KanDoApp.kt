package com.alexgi.kando

import android.app.Application
import com.alexgi.kando.data.local.TaskDbHelper
import com.alexgi.kando.data.remote.TodoApiService
import com.alexgi.kando.viewmodel.TaskViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class KanDoApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@KanDoApp)
            modules(appModule)
        }
    }
}

val appModule = module {
    single { TaskDbHelper(get()) }
    single { TodoApiService.create() }
    viewModel { TaskViewModel(get(), get()) }
}
