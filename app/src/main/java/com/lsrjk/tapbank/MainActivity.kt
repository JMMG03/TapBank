@file:OptIn(ExperimentalMaterial3Api::class)

package com.lsrjk.tapbank

import android.content.Context
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

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
    val haptics by vm.hapticsEnabled.collectAsState()
    var isPressed by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    // auto-incremento mientras mantienes pulsado
    LaunchedEffect(isPressed) {
        if (isPressed) {
            while (isPressed) {
                vm.increment()
                if (haptics) vibrateOnce(context)
                delay(150)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TapBank") },
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_preferences),
                            contentDescription = "Ajustes"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            try {
                                tryAwaitRelease()
                            } finally {
                                isPressed = false
                            }
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

        if (showSettings) {
            SettingsDialog(
                hapticsEnabled = haptics,
                onToggleHaptics = { vm.toggleHaptics() },
                onDismiss = { showSettings = false }
            )
        }
    }
}

@Composable
fun SettingsDialog(hapticsEnabled: Boolean, onToggleHaptics: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { Text(text = "OK", modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { onDismiss() }) }) },
        title = { Text("Ajustes") },
        text = {
            androidx.compose.foundation.layout.Column {
                androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Vibración háptica", modifier = Modifier.weight(1f))
                    Switch(checked = hapticsEnabled, onCheckedChange = { onToggleHaptics() })
                }
                Text("• Toque: +1  • Mantener: auto-suma  • Long press: reset")
            }
        }
    )
}

// Vibración corta (respetando el toggle)
fun vibrateOnce(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(30)
    }
}
