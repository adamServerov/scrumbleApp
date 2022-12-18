package com.example.android.unscramble.ui

import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.android.unscramble.data.MAX_NO_OF_WORDS
import com.example.android.unscramble.data.SCORE_INCREASE
import com.example.android.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {

    private var _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

     var userGuess by mutableStateOf("")
        private set
    val usedWords = mutableSetOf<String>()
    lateinit var currentWord: String

    init {
        resetGame()
    }

    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambleWord = pickupRandomWordAndShuffle())
    }

    fun skipWord() {
      updateGameState(_uiState.value.score)
        updateUserGuess("")
    }

    fun updateUserGuess(gussedWord: String) {
        userGuess = gussedWord
    }

    fun checkUserGuess() {
        if (userGuess.equals(currentWord, ignoreCase = true)) {
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updatedScore)
        } else {
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
            updateUserGuess("")
        }
    }

    fun updateGameState(updatedScope: Int) {
        if (usedWords.size == MAX_NO_OF_WORDS) {
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    currentWordCount = currentState.currentWordCount,
                    score = updatedScope,
                    isGameOver = true
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    currentScrambleWord = currentState.currentScrambleWord,
                    isGuessedWordWrong = false,
                    currentWordCount = currentState.currentWordCount,
                    score = updatedScope,

                    )
            }
        }
    }

    private fun shuffleRandomWord(word: String): String {
        val tempWord = word.toCharArray()
        tempWord.shuffle()
        while (String(tempWord).equals(word)) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    fun pickupRandomWordAndShuffle(): String {
        currentWord = allWords.random()
        if (usedWords.contains(currentWord)) {
            return pickupRandomWordAndShuffle()
        } else {
            return shuffleRandomWord(currentWord)
        }

    }


}