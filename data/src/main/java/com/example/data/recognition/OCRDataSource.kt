package com.example.data.recognition

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class OCRDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun recognizeText(uri: Uri): String = suspendCoroutine { cont ->
        val image = InputImage.fromFilePath(context, uri)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val sb = StringBuilder()
                for (block in visionText.textBlocks) {
                    for (line in block.lines) {
                        sb.append(line.text).append("\n")
                        println("qwqwqw LINE: ${line.text}")
                    }
                }
                val result = sb.toString()
                cont.resume(result)
            }
            .addOnFailureListener { cont.resumeWithException(it) }
    }
}