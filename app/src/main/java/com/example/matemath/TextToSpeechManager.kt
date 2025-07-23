package com.example.matemath

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class TextToSpeechManager(context: Context) : TextToSpeech.OnInitListener {
    
    private var tts: TextToSpeech? = null
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()
    
    init {
        tts = TextToSpeech(context, this)
    }
    
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let { textToSpeech ->
                val result = textToSpeech.setLanguage(Locale("es", "ES"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Spanish language not supported, trying generic Spanish")
                    textToSpeech.setLanguage(Locale("es"))
                }
                
                // Configure TTS settings for children
                textToSpeech.setSpeechRate(0.8f) // Slightly slower for comprehension
                textToSpeech.setPitch(1.2f) // Slightly higher pitch for friendliness
                
                _isReady.value = true
                Log.d("TTS", "Text-to-Speech initialized successfully")
            }
        } else {
            Log.e("TTS", "Text-to-Speech initialization failed")
        }
    }
    
    fun speak(text: String) {
        if (_isReady.value) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        }
    }
    
    fun speakMathProblem(problem: MathProblem) {
        val operationWord = when (problem.operation) {
            MathOperation.ADDITION -> "más"
            MathOperation.SUBTRACTION -> "menos"
            MathOperation.MULTIPLICATION -> "por"
            MathOperation.DIVISION -> "dividido entre"
        }
        
        val questionText = "¿Cuánto es ${problem.firstNumber} $operationWord ${problem.secondNumber}?"
        speak(questionText)
    }
    
    fun speakEncouragement(isCorrect: Boolean) {
        val message = if (isCorrect) {
            listOf("¡Muy bien!", "¡Excelente!", "¡Perfecto!", "¡Genial!").random()
        } else {
            listOf("¡Inténtalo de nuevo!", "¡Casi!", "¡Tú puedes!", "¡Sigue intentando!").random()
        }
        speak(message)
    }
    
    fun stop() {
        tts?.stop()
    }
    
    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
} 