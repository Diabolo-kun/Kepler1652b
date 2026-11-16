package com.example.mpergar9.kepler1652b.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpergar9.kepler1652b.data.CampaignRepository
import com.example.mpergar9.kepler1652b.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit
) {
    var isRegisterMode by remember { mutableStateOf(false) }

    var displayNameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = VoidBlack,
        modifier = Modifier.statusBarsPadding()
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyanCurvature.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Título Principal
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "KEPLER-1652b",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = CyanCurvature,
                            fontSize = 22.sp,
                            letterSpacing = 3.sp
                        )
                        Text(
                            text = "Terminal de Acceso e Identificación",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    // Selector Modo: Iniciar Sesión / Registro
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = !isRegisterMode,
                            onClick = {
                                isRegisterMode = false
                                errorMessage = null
                            },
                            label = { Text("🔑 Iniciar Sesión", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanCurvature,
                                selectedLabelColor = VoidBlack
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = isRegisterMode,
                            onClick = {
                                isRegisterMode = true
                                errorMessage = null
                            },
                            label = { Text("🚀 Crear Cuenta", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonCyan,
                                selectedLabelColor = VoidBlack
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider(color = PanelBorder.copy(alpha = 0.5f))

                    // Mensaje de Error
                    errorMessage?.let { err ->
                        Surface(
                            color = DangerRed.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, DangerRed),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = err,
                                color = DangerRed,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // Campos Formulario
                    if (isRegisterMode) {
                        OutlinedTextField(
                            value = displayNameInput,
                            onValueChange = { displayNameInput = it },
                            label = { Text("Nombre de Colono / Usuario") },
                            placeholder = { Text("Ej: Cmdte. Vance") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanCurvature,
                                unfocusedBorderColor = PanelBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Correo Electrónico") },
                        placeholder = { Text("colono@kepler1652b.com") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanCurvature,
                            unfocusedBorderColor = PanelBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanCurvature,
                            unfocusedBorderColor = PanelBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    if (isRegisterMode) {
                        OutlinedTextField(
                            value = confirmPasswordInput,
                            onValueChange = { confirmPasswordInput = it },
                            label = { Text("Confirmar Contraseña") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanCurvature,
                                unfocusedBorderColor = PanelBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    if (isLoading) {
                        CircularProgressIndicator(color = CyanCurvature, modifier = Modifier.size(32.dp))
                    } else {
                        Button(
                            onClick = {
                                errorMessage = null
                                val email = emailInput.trim()
                                val password = passwordInput.trim()

                                if (email.isBlank() || password.isBlank()) {
                                    errorMessage = "Por favor, introduce correo electrónico y contraseña."
                                    return@Button
                                }

                                if (isRegisterMode) {
                                    val name = displayNameInput.trim()
                                    if (name.isBlank()) {
                                        errorMessage = "Por favor, introduce tu nombre de colono."
                                        return@Button
                                    }
                                    if (password.length < 6) {
                                        errorMessage = "La contraseña debe tener al menos 6 caracteres."
                                        return@Button
                                    }
                                    if (password != confirmPasswordInput.trim()) {
                                        errorMessage = "Las contraseñas no coinciden."
                                        return@Button
                                    }

                                    isLoading = true
                                    CampaignRepository.registerUserWithEmail(
                                        email = email,
                                        password = password,
                                        displayName = name,
                                        onResult = { success, error ->
                                            isLoading = false
                                            if (success) {
                                                onAuthSuccess()
                                            } else {
                                                errorMessage = error ?: "Error durante el registro."
                                            }
                                        }
                                    )
                                } else {
                                    isLoading = true
                                    CampaignRepository.loginUserWithEmail(
                                        email = email,
                                        password = password,
                                        onResult = { success, error ->
                                            isLoading = false
                                            if (success) {
                                                onAuthSuccess()
                                            } else {
                                                errorMessage = error ?: "Credenciales incorrectas o usuario no encontrado."
                                            }
                                        }
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanCurvature, contentColor = VoidBlack)
                        ) {
                            Text(
                                text = if (isRegisterMode) "🚀 Registrar Colono" else "🔑 Acceder a la Misión",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
