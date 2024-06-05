package com.example.mynfc.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mynfc.AppLayout
import com.example.mynfc.R
import com.example.mynfc.ui.voda.VodaUiState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: State<VodaUiState>,
    navController: NavHostController,
    changeLocale: (context: Context, newLocale: String) -> Unit,
) {
    AppLayout(navController = navController) {
        val context = LocalContext.current
        val languageMap = mapOf("ru" to "Русский", "zh" to "中文")
        var expanded by remember { mutableStateOf(false) }
        val currentLocale = uiState.value.currentLocale
        println("currentLocale: $currentLocale")
        var newLocale: String

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row {
                TextField(
                    value = languageMap[currentLocale] ?: "ru",
                    onValueChange = {},
                    readOnly = true,
                    leadingIcon = {
                        Image(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(id = when (currentLocale) {
                                "ru" -> R.drawable.russia
                                "zh" -> R.drawable.china
                                else -> R.drawable.china
                            }
                            ),
                            contentDescription = "")
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
            }
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                languageMap.forEach { language ->
                    DropdownMenuItem(
                        text = { Text(text = language.value) },
                        onClick = {
                            expanded = false
                            newLocale = language.key
                            changeLocale(context, newLocale)
                        }
                    )
                }
            }
        }

    }
}