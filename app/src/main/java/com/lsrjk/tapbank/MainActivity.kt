package com.lsrjk.tapbank

import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                val vm: CounterViewModel = viewModel(factory = CounterViewModel.factory(applicationContext))
                TapBankScreen(vm, this)
            }
        }
    }
}

@Composable
fun TapBankScreen(vm: CounterViewModel, context: Context) {
    val count by vm.count.collectAsState()
    val scope = rememberCoroutineScope()
    var isPressed by remember { mutableStateOf(false) }

    // bucle auto-incremento
    LaunchedEffect(isPressed) {
        if (isPressed) {
            while (isPressed) {
                vm.increment()
                vibrateOnce(context) // vibración cada toque
                delay(150) // velocidad de auto-incremento
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                        },
                        onLongPress = { vm.reset() } // reset en pulsación larga
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                fontSize = 84.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

// 🔔 vibración corta
fun vibrateOnce(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(30)
    }
}
