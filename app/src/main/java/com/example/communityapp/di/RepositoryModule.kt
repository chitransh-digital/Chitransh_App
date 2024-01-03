package com.example.communityapp.di

import com.example.communityapp.data.repository.FeedsRepo
import com.example.communityapp.data.repository.JobsRepo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun provideJobsRepository(): JobsRepo {
        return JobsRepo(FirebaseFirestore.getInstance())
    }

    @Provides
    @Singleton
    fun provideFeedsRepository(): FeedsRepo {
        return FeedsRepo(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance())
    }

}