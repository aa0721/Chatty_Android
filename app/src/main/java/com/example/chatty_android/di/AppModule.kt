package com.example.chatty_android.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.chatty_android.common.constants.Constants
import com.example.chatty_android.data.dao.FriendRelationDao
import com.example.chatty_android.data.dao.MessageDao
import com.example.chatty_android.data.dao.UserDao
import com.example.chatty_android.data.database.ChattyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.PREFERENCES_NAME)

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ChattyDatabase {
        return Room.databaseBuilder(
            context,
            ChattyDatabase::class.java,
            Constants.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: ChattyDatabase): UserDao = database.userDao()

    @Provides
    @Singleton
    fun provideFriendRelationDao(database: ChattyDatabase): FriendRelationDao = database.friendRelationDao()

    @Provides
    @Singleton
    fun provideMessageDao(database: ChattyDatabase): MessageDao = database.messageDao()
}
