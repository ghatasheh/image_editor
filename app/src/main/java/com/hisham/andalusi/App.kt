package com.hisham.andalusi

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hisham.andalusi.ui.theme.AndalusiTheme

@Composable
fun App() {
    AndalusiTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            EditorScreen(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}