package com.example.helpdeskchatapp.di

import com.example.helpdeskchatapp.data.interfaces.AdminRepository
import com.example.helpdeskchatapp.data.interfaces.ChatRepository
import com.example.helpdeskchatapp.data.interfaces.UserRepository
import com.example.helpdeskchatapp.data.repository.FirestoreAdminRepository
import com.example.helpdeskchatapp.data.repository.FirestoreChatRepository
import com.example.helpdeskchatapp.data.repository.FirebaseUserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        firebaseUserRepository: FirebaseUserRepository
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindAdminRepository(
        firestoreAdminRepository: FirestoreAdminRepository
    ): AdminRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        firestoreChatRepository: FirestoreChatRepository
    ): ChatRepository
}
