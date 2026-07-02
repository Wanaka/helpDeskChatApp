package haag.your.next.developer.di

import haag.your.next.developer.data.interfaces.AdminRepository
import haag.your.next.developer.data.interfaces.ChatRepository
import haag.your.next.developer.data.interfaces.ReadTimestampRepository
import haag.your.next.developer.data.interfaces.UserRepository
import haag.your.next.developer.data.repository.FirestoreAdminRepository
import haag.your.next.developer.data.repository.FirestoreChatRepository
import haag.your.next.developer.data.repository.FirebaseUserRepository
import haag.your.next.developer.data.repository.LocalReadTimestampRepository
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

    @Binds
    @Singleton
    abstract fun bindReadTimestampRepository(
        localReadTimestampRepository: LocalReadTimestampRepository
    ): ReadTimestampRepository
}
