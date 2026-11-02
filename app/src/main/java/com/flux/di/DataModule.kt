package com.flux.di

import android.content.Context
import androidx.room.Room
import com.flux.data.database.FluxDatabase
import com.flux.plan.data.PlanDao
import com.flux.ritual.data.RitualDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Singleton
    @Provides
    fun provideFluxDatabase(
        @ApplicationContext app: Context
    ): FluxDatabase = Room.databaseBuilder(
        app,
        FluxDatabase::class.java,
        "FluxDatabase"
    )
        .fallbackToDestructiveMigration()
        .build()

    @Singleton
    @Provides
    fun provideRitualDao(db: FluxDatabase): RitualDao = db.ritualDao

    @Singleton
    @Provides
    fun providePlanDao(db: FluxDatabase): PlanDao = db.planDao
}
