package com.harsh.calculator

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.harsh.calculator.ui.theme.CalculatorTheme
import net.objecthunter.exp4j.ExpressionBuilder
import java.util.Locale
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorTheme {
                Calculator()
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun Calculator() {
    val context = LocalContext.current
    val speechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context)
    }
    val speech = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    }

    val mediaPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = Manifest.permission.READ_MEDIA_VIDEO)
    } else {
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    var equation by remember {
        mutableStateOf("")
    }

    var result by remember {
        mutableStateOf("")
    }

    var speechBegin by remember {
        mutableStateOf(false)
    }

    var askPermission by remember {
        mutableStateOf(false)
    }

    val mediaPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        askPermission = it
    }

    fun containsBasic(): Boolean {
        return if (equation.contains("+")) {
            true
        } else if (equation.contains("—")) {
            true
        } else if (equation.contains("✕")) {
            true
        } else {
            false
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            speechRecognizer.destroy()
        }
    }

    LaunchedEffect(key1 = mediaPermission) {
        if (!mediaPermission.status.isGranted) {
            askPermission = true
        } else {
            askPermission = false
        }
    }

    if (askPermission) {
        mediaPermissionLauncher.launch(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.RECORD_AUDIO
            } else {
                Manifest.permission.RECORD_AUDIO
            }
        )
    }

    LaunchedEffect(Unit) {
        speech.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(p0: Bundle?) {

            }

            override fun onBeginningOfSpeech() {
                speechBegin = true
            }

            override fun onRmsChanged(p0: Float) {

            }

            override fun onBufferReceived(p0: ByteArray?) {

            }

            override fun onEndOfSpeech() {
                speechBegin = false
            }

            override fun onError(p0: Int) {
                speechBegin = false
            }

            override fun onResults(p0: Bundle?) {
                val data = p0!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val value = if (!data.isNullOrEmpty()) data[0] else "No Command"

                Log.e("SZ", value.toString())

                equation = value.toString()
            }

            override fun onPartialResults(p0: Bundle?) {

            }

            override fun onEvent(p0: Int, p1: Bundle?) {

            }
        })
    }

    Box {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(500.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0xB5070606))
                .padding(bottom = 15.dp)
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 15.dp)
                    )
                },
                colors = TopAppBarColors(
                    Color.Transparent,
                    Color.Transparent,
                    Color.White,
                    Color.White,
                    Color.White
                )
            )
            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(), horizontalAlignment = Alignment.End
            ) {
                IconButton(
                    onClick = {
                        if (speechBegin) {
                            speechRecognizer.stopListening()
                        } else {
                            speechRecognizer.startListening(speech)
                        }
                    },
                    modifier = Modifier
                        .padding(bottom = 30.dp)
                        .background(
                            if (speechBegin) Color(0x19FFFFFF) else Color(0x08FFFFFF),
                            CircleShape
                        ),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.microphone),
                        contentDescription = "microphone",
                        tint = Color.White
                    )
                }
                Text(
                    text = equation,
                    color = Color(0x95FFFFFF),
                    fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                    fontSize = 26.sp
                )
                Text(
                    text = result,
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.montserrat_bold)),
                    fontSize = 50.sp,
                    letterSpacing = 5.sp,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = Color(0x08FFFFFF),
                modifier = Modifier.padding(top = 20.dp)
            )
            Column(modifier = Modifier.padding(bottom = 10.dp)) {
                Row(modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp)) {
                    Button(
                        onClick = {
                            equation += "%"
                        },
                        colors = ButtonColors(
                            Color(0x08FFFFFF),
                            Color.White,
                            Color(0x08FFFFFF),
                            Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 20.dp)
                    ) {
                        Text(
                            text = "%",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                            fontSize = 18.sp,
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            equation = ""
                        },
                        colors = ButtonColors(
                            Color(0x08FFFFFF),
                            Color.White,
                            Color(0x08FFFFFF),
                            Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 20.dp)
                    ) {
                        Text(
                            text = "CE",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                            fontSize = 18.sp,
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            equation = ""
                            result = ""
                        },
                        colors = ButtonColors(
                            Color(0x08FFFFFF),
                            Color.White,
                            Color(0x08FFFFFF),
                            Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 20.dp)
                    ) {
                        Text(
                            text = "C",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                            fontSize = 18.sp,
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            if (equation.isNotEmpty()) {
                                equation = equation.dropLast(1)
                            }
                        },
                        colors = ButtonColors(
                            Color(0x08FFFFFF),
                            Color.White,
                            Color(0x08FFFFFF),
                            Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 20.dp)
                    ) {
                        Text(
                            text = "⌫",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                            fontSize = 18.sp,
                        )
                    }
                }
                Row(modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp)) {
                    Button(
                        onClick = {
                            if (equation.isEmpty()) {
                                result = "Cannot divide by zero"
                            } else {
                                if (containsBasic()) {
                                    equation += "1/($result)"
                                } else {
                                    equation = "1/($result)"
                                }
                            }
                        },
                        colors = ButtonColors(
                            Color(0x08FFFFFF),
                            Color.White,
                            Color(0x08FFFFFF),
                            Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 20.dp)
                    ) {
                        Text(
                            text = "⅟x",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                            fontSize = 18.sp,
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            if (equation.isEmpty()) {
                                equation = "0^2"
                            } else {
                                equation = "$equation^2"
                            }
                        },
                        colors = ButtonColors(
                            Color(0x08FFFFFF),
                            Color.White,
                            Color(0x08FFFFFF),
                            Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 20.dp)
                    ) {
                        Text(
                            text = "x²",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                            fontSize = 18.sp,
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            if (equation.isEmpty()) {
                                equation = "√(0)"
                            } else {
                                equation = "√($equation)"
                            }
                        },
                        colors = ButtonColors(
                            Color(0x08FFFFFF),
                            Color.White,
                            Color(0x08FFFFFF),
                            Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 20.dp)
                    ) {
                        Text(
                            text = "²√x",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                            fontSize = 18.sp,
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            equation += "+"
                        },
                        colors = ButtonColors(
                            Color(0x08FFFFFF),
                            Color.White,
                            Color(0x08FFFFFF),
                            Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 20.dp)
                    ) {
                        Text(
                            text = "+",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                            fontSize = 18.sp,
                        )
                    }
                }
                Row(modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp)) {
                    Button(
                        onClick = {
                            equation += "7"
                            result = "7"
                        },
                        colors = ButtonColors(
                            Color(0x08FFFFFF),
                            Color.White,
                            Color(0x08FFFFFF),
                            Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 20.dp)
                    ) {
                        Text(
                            text = "7",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                            fontSize = 18.sp,
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            equation += "8"
                            result = "8"
                        },
                        colors = ButtonColors(
                            Color(0x08FFFFFF),
                            Color.White,
                            Color(0x08FFFFFF),
                            Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 20.dp)
                    ) {
                        Text(
                            text = "8",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                            fontSize = 18.sp,
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            equation += "9"
                            result = "9"
                        },
                        colors = ButtonColors(
                            Color(0x08FFFFFF),
                            Color.White,
                            Color(0x08FFFFFF),
                            Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 20.dp)
                    ) {
                        Text(
                            text = "9",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                            fontSize = 18.sp,
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            equation += "÷"
                        },
                        colors = ButtonColors(
                            Color(0x08FFFFFF),
                            Color.White,
                            Color(0x08FFFFFF),
                            Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        Text(
                            text = "÷",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                            fontSize = 26.sp,
                        )
                    }
                }
                Row(modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp)) {
                    Button(
                        onClick = {
                            equation += "4"
                            result = "4"
                        },
                        colors = ButtonColors(
                            Color(0x08FFFFFF),
                            Color.White,
                            Color(0x08FFFFFF),
                            Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 20.dp)
                    ) {
                        Text(
                            text = "4",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                            fontSize = 18.sp,
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            equation += "5"
                            result = "5"
                        },
                        colors = ButtonColors(
                            Color(0x08FFFFFF),
                            Color.White,
                            Color(0x08FFFFFF),
                            Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 20.dp)
                    ) {
                        Text(
                            text = "5",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                            fontSize = 18.sp,
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            equation += "6"
                            result = "6"
                        },
                        colors = ButtonColors(
                            Color(0x08FFFFFF),
                            Color.White,
                            Color(0x08FFFFFF),
                            Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 20.dp)
                    ) {
                        Text(
                            text = "6",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                            fontSize = 18.sp,
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            equation += "✕"
                        },
                        colors = ButtonColors(
                            Color(0x08FFFFFF),
                            Color.White,
                            Color(0x08FFFFFF),
                            Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 20.dp)
                    ) {
                        Text(
                            text = "✕",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                            fontSize = 18.sp,
                        )
                    }
                }
                Row(modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp)) {
                    Button(
                        onClick = {
                            equation += "1"
                            result = "1"
                        },
                        colors = ButtonColors(
                            Color(0x08FFFFFF),
                            Color.White,
                            Color(0x08FFFFFF),
                            Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 20.dp)
                    ) {
                        Text(
                            text = "1",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                            fontSize = 18.sp,
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            equation += "2"
                            result = "2"
                        },
                        colors = ButtonColors(
                            Color(0x08FFFFFF),
                            Color.White,
                            Color(0x08FFFFFF),
                            Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 20.dp)
                    ) {
                        Text(
                            text = "2",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                            fontSize = 18.sp,
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            equation += "3"
                            result = "3"
                        },
                        colors = ButtonColors(
                            Color(0x08FFFFFF),
                            Color.White,
                            Color(0x08FFFFFF),
                            Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 20.dp)
                    ) {
                        Text(
                            text = "3",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                            fontSize = 18.sp,
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            equation += "—"
                        },
                        colors = ButtonColors(
                            Color(0x08FFFFFF),
                            Color.White,
                            Color(0x08FFFFFF),
                            Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 20.dp)
                    ) {
                        Text(
                            text = "—",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                            fontSize = 18.sp,
                        )
                    }
                }
                Row(modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp)) {
                    Button(
                        onClick = {
                            equation += "⁺∕₋"
                        },
                        colors = ButtonColors(
                            Color(0x08FFFFFF),
                            Color.White,
                            Color(0x08FFFFFF),
                            Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 20.dp)
                    ) {
                        Text(
                            text = "⁺∕₋",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                            fontSize = 18.sp,
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            equation += "0"
                        },
                        colors = ButtonColors(
                            Color(0x08FFFFFF),
                            Color.White,
                            Color(0x08FFFFFF),
                            Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 20.dp)
                    ) {
                        Text(
                            text = "0",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                            fontSize = 18.sp,
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            equation += "."
                        },
                        colors = ButtonColors(
                            Color(0x08FFFFFF),
                            Color.White,
                            Color(0x08FFFFFF),
                            Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 20.dp)
                    ) {
                        Text(
                            text = ".",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                            fontSize = 18.sp,
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            try {
                                val sanitizedEquation = equation.replace("÷", "/")
                                    .replace("✕", "*")
                                    .replace("—", "-")
                                    .replace("⁺∕₋", "-")
                                    //.replace("⅟x", "1/x")
                                    .replace("√", "sqrt")

                                val expr = ExpressionBuilder(sanitizedEquation).build()
                                val evalResult = expr.evaluate()

                                result = if (evalResult % 1 == 0.0) {
                                    evalResult.toInt().toString()
                                } else {
                                    String.format("%.2f", evalResult)
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                Log.e("ERROR", e.message.toString())
                            }
                        },
                        colors = ButtonColors(
                            Color(0xFF430D92),
                            Color.White,
                            Color(0x08FFFFFF),
                            Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 20.dp)
                    ) {
                        Text(
                            text = "=",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                            fontSize = 18.sp,
                        )
                    }
                }
            }
        }
    }
}