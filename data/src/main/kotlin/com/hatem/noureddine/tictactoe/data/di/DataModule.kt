package com.hatem.noureddine.tictactoe.data.di

import com.hatem.noureddine.tictactoe.data.repository.GameRepositoryImpl
import com.hatem.noureddine.tictactoe.domain.repository.GameRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing Data layer dependencies.
 *
 * Binds the implementation of [GameRepository] to [GameRepositoryImpl].
 * Installed in [SingletonComponent] to ensure the repository singleton lives for the app's duration.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindGameRepository(impl: GameRepositoryImpl): GameRepository
}
