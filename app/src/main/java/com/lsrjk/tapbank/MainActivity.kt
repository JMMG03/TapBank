package com.lsrjk.tapbank

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.gestures.detectTapGestures


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                val vm: CounterViewModel = viewModel(factory = CounterViewModel.factory(applicationContext))
                TapBankScreen(vm)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TapBankScreen(vm: CounterViewModel) {
    val count by vm.count.collectAsState()
    // Pantalla completa táctil
    Surface(
        modifier = Modifier
            .pointerInteropFilter {
                if (it.action == android.view.MotionEvent.ACTION_DOWN) {
                    vm.increment()
                }
                true
            },
        color = MaterialTheme.colorScheme.background
    ) {
        // Texto central enorme
        BoxedCount(
            count = count,
            onLongPress = { vm.reset() } // reset opcional con pulsación larga
        )
    }
}

@Composable
fun BoxedCount(count: Long, onLongPress: () -> Unit) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                // Long press para reset (opcional)
                detectTapGestures(
                    onLongPress = { onLongPress() }
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
