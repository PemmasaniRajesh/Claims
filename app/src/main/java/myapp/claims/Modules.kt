package myapp.claims

import myapp.claims.Database.AppDatabase
import myapp.claims.Database.ClaimsRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModules= module {
    single { AppDatabase.getInstance(get())}
    single { ClaimsRepository(get()) }
    viewModel { ClaimsDataViewModel(get()) }
}