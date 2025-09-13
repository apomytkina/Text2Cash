package com.example.text2cash

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
internal fun HomeScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Translator & Currency", style = MaterialTheme.typography.bodyLarge)

        val uiState = null
        DropdownMenu(false, onDismissRequest = {}, content = {})
        DropdownMenu(false, onDismissRequest = {}, content = {})

        Button(onClick = { navController.navigate("camera") }) {
            Text("Scan")
        }
    }
}