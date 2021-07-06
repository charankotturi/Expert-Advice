package com.sales.dashboard.application

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sales.dashboard.database.ProfileDao
import com.sales.dashboard.database.ProfileDataBase
import com.sales.dashboard.repository.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.DefineComponent
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(
        context: Application
    ) = Room.databaseBuilder(context, ProfileDataBase::class.java, "USERS")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideDao(db : ProfileDataBase) =
        db.profileDao()

    @Provides
    fun provideProfileRepo(dao : ProfileDao) =
        ProfileRepository(dao)
}