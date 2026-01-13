package com.hatem.noureddine.tictactoe.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * The custom Application class for the Tic Tac Toe app.
 *
 * It is annotated with @HiltAndroidApp to trigger Hilt's code generation,
 * serving as the application-level dependency container.
 */
@HiltAndroidApp
class TicTacToeApplication : Application()
