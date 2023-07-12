package com.faesfa.tiwo.core.di

import android.content.Context
import androidx.room.Room
import com.faesfa.tiwo.data.database.PresetsDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    private const val PRESETS_DATABASE = "presets_database"

    @Singleton
    @Provides
    fun providesRoom(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, PresetsDataBase::class.java, PRESETS_DATABASE).build()

    @Singleton
    @Provides
    fun providePresetsDao(db:PresetsDataBase) = db.getPresetDao()
}