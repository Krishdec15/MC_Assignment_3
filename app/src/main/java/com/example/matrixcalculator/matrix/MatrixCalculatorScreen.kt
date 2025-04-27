package com.example.matrixcalculator.matrix

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MatrixCalculatorScreen(onBackClick: () -> Unit) {
    var rows1 by remember { mutableStateOf("") }
    var cols1 by remember { mutableStateOf("") }
    var rows2 by remember { mutableStateOf("") }
    var cols2 by remember { mutableStateOf("") }
    var matrix1 by remember { mutableStateOf("") }
    var matrix2 by remember { mutableStateOf("") }
    var selectedOperation by remember { mutableStateOf("Add") }
    var isOperationDropdownExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var matrixResult by remember { mutableStateOf("") }
    var resultRows by remember { mutableStateOf(0) }
    var resultCols by remember { mutableStateOf(0) }

    val colorScheme = MaterialTheme.colorScheme
    val textColor = colorScheme.onBackground
    val errorColor = colorScheme.error
    val textStyle = TextStyle(color = textColor)
    val cardBackgroundColor = colorScheme.surface

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Matrix Calculator",
            style = TextStyle(
                color = textColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = rows1,
                        onValueChange = { 
                            // Add validation for numbers only
                            if (it.isEmpty() || it.matches(Regex("^\\d+$"))) {
                                rows1 = it
                            }
                        },
                        label = { Text("Matrix 1 Rows") },
                        isError = rows1.toIntOrNull() == null && rows1.isNotEmpty(),
                        textStyle = textStyle,
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent,
                            focusedIndicatorColor = colorScheme.primary,
                            unfocusedIndicatorColor = colorScheme.outline,
                            errorIndicatorColor = errorColor,
                            cursorColor = colorScheme.primary
                        )
                    )
                    OutlinedTextField(
                        value = cols1,
                        onValueChange = { 
                            // Add validation for numbers only
                            if (it.isEmpty() || it.matches(Regex("^\\d+$"))) {
                                cols1 = it
                            }
                        },
                        label = { Text("Matrix 1 Cols") },
                        isError = cols1.toIntOrNull() == null && cols1.isNotEmpty(),
                        textStyle = textStyle,
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent,
                            focusedIndicatorColor = colorScheme.primary,
                            unfocusedIndicatorColor = colorScheme.outline,
                            errorIndicatorColor = errorColor,
                            cursorColor = colorScheme.primary
                        )
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = rows2,
                        onValueChange = { 
                            // Add validation for numbers only
                            if (it.isEmpty() || it.matches(Regex("^\\d+$"))) {
                                rows2 = it
                            }
                        },
                        label = { Text("Matrix 2 Rows") },
                        isError = rows2.toIntOrNull() == null && rows2.isNotEmpty(),
                        textStyle = textStyle,
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent,
                            focusedIndicatorColor = colorScheme.primary,
                            unfocusedIndicatorColor = colorScheme.outline,
                            errorIndicatorColor = errorColor,
                            cursorColor = colorScheme.primary
                        )
                    )
                    OutlinedTextField(
                        value = cols2,
                        onValueChange = { 
                            // Add validation for numbers only
                            if (it.isEmpty() || it.matches(Regex("^\\d+$"))) {
                                cols2 = it
                            }
                        },
                        label = { Text("Matrix 2 Cols") },
                        isError = cols2.toIntOrNull() == null && cols2.isNotEmpty(),
                        textStyle = textStyle,
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent,
                            focusedIndicatorColor = colorScheme.primary,
                            unfocusedIndicatorColor = colorScheme.outline,
                            errorIndicatorColor = errorColor,
                            cursorColor = colorScheme.primary
                        )
                    )
                }
                
                // Helper text for input format
                Text(
                    text = "For matrix values, enter numbers separated by commas (e.g. 1,2,3,4)",
                    color = colorScheme.onSurfaceVariant,
                    style = TextStyle(fontSize = 12.sp),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                
                OutlinedTextField(
                    value = matrix1,
                    onValueChange = { matrix1 = it },
                    label = { Text("Matrix 1 (comma-separated)") },
                    textStyle = textStyle,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        focusedIndicatorColor = colorScheme.primary,
                        unfocusedIndicatorColor = colorScheme.outline,
                        errorIndicatorColor = errorColor,
                        cursorColor = colorScheme.primary
                    )
                )
                OutlinedTextField(
                    value = matrix2,
                    onValueChange = { matrix2 = it },
                    label = { Text("Matrix 2 (comma-separated)") },
                    textStyle = textStyle,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        focusedIndicatorColor = colorScheme.primary,
                        unfocusedIndicatorColor = colorScheme.outline,
                        errorIndicatorColor = errorColor,
                        cursorColor = colorScheme.primary
                    )
                )
                
                // Operation selection
                Text(
                    text = "Select Operation",
                    style = TextStyle(
                        color = textColor,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
                
                Box {
                    ElevatedButton(
                        onClick = { isOperationDropdownExpanded = true },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedOperation)
                    }
                    DropdownMenu(
                        expanded = isOperationDropdownExpanded,
                        onDismissRequest = { isOperationDropdownExpanded = false },
                        modifier = Modifier.width(150.dp)
                    ) {
                        listOf("Add", "Subtract", "Multiply", "Divide").forEach { op ->
                            DropdownMenuItem(
                                text = { Text(op) },
                                onClick = {
                                    selectedOperation = op
                                    isOperationDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        ElevatedButton(
            onClick = {
                errorMessage = ""
                matrixResult = ""
                resultRows = 0
                resultCols = 0
                Log.d("MatrixCalc", "Inputs: r1=$rows1, c1=$cols1, r2=$rows2, c2=$cols2, m1=$matrix1, m2=$matrix2, op=$selectedOperation")
                try {
                    if (rows1.isEmpty() || cols1.isEmpty() || rows2.isEmpty() || cols2.isEmpty()) {
                        errorMessage = "All dimension fields are required"
                        return@ElevatedButton
                    }
                    val r1 = rows1.toIntOrNull() ?: run {
                        errorMessage = "Invalid Matrix 1 rows"
                        return@ElevatedButton
                    }
                    val c1 = cols1.toIntOrNull() ?: run {
                        errorMessage = "Invalid Matrix 1 columns"
                        return@ElevatedButton
                    }
                    val r2 = rows2.toIntOrNull() ?: run {
                        errorMessage = "Invalid Matrix 2 rows"
                        return@ElevatedButton
                    }
                    val c2 = cols2.toIntOrNull() ?: run {
                        errorMessage = "Invalid Matrix 2 columns"
                        return@ElevatedButton
                    }
                    if (r1 <= 0 || c1 <= 0 || r2 <= 0 || c2 <= 0) {
                        errorMessage = "Dimensions must be positive"
                        return@ElevatedButton
                    }
                    if (matrix1.isEmpty() || matrix2.isEmpty()) {
                        errorMessage = "Matrix inputs cannot be empty"
                        return@ElevatedButton
                    }
                    
                    // Parse and validate matrix values
                    val m1Values = matrix1.split(",").map { it.trim() }
                    if (m1Values.size != r1 * c1) {
                        errorMessage = "Matrix 1: Expected ${r1 * c1} values, got ${m1Values.size}"
                        return@ElevatedButton
                    }
                    
                    val m2Values = matrix2.split(",").map { it.trim() }
                    if (m2Values.size != r2 * c2) {
                        errorMessage = "Matrix 2: Expected ${r2 * c2} values, got ${m2Values.size}"
                        return@ElevatedButton
                    }
                    
                    val m1 = m1Values.map { it.toDoubleOrNull() ?: run {
                        errorMessage = "Invalid value in Matrix 1: $it"
                        return@ElevatedButton
                    } }.toDoubleArray()
                    
                    val m2 = m2Values.map { it.toDoubleOrNull() ?: run {
                        errorMessage = "Invalid value in Matrix 2: $it"
                        return@ElevatedButton
                    } }.toDoubleArray()
                    
                    if (selectedOperation == "Multiply" || selectedOperation == "Divide") {
                        if (c1 != r2) {
                            errorMessage = "Matrix 1 columns must equal Matrix 2 rows for ${selectedOperation.lowercase()}"
                            return@ElevatedButton
                        }
                        if (selectedOperation == "Divide" && r2 != c2) {
                            errorMessage = "Matrix 2 must be square for division"
                            return@ElevatedButton
                        }
                        
                        // Additional check for division - matrix 2 must be invertible
                        if (selectedOperation == "Divide" && r2 == 2 && c2 == 2) {
                            val determinant = m2[0] * m2[3] - m2[1] * m2[2]
                            if (determinant == 0.0) {
                                errorMessage = "Matrix 2 is not invertible (determinant = 0)"
                                return@ElevatedButton
                            }
                        }
                        
                        val result = DoubleArray(r1 * c2)
                        if (selectedOperation == "Multiply") {
                            MatrixOperations.multiplyMatrices(m1, m2, result, r1, c1, r2, c2)
                        } else {
                            MatrixOperations.divideMatrices(m1, m2, result, r1, c1, r2, c2)
                        }
                        matrixResult = result.joinToString(",").also { Log.d("MatrixCalc", "Result: $it, rows=$r1, cols=$c2") }
                        resultRows = r1
                        resultCols = c2
                    } else {
                        if (r1 != r2 || c1 != c2) {
                            errorMessage = "Matrices must have same dimensions for addition/subtraction"
                            return@ElevatedButton
                        }
                        
                        val result = DoubleArray(r1 * c1)
                        when (selectedOperation) {
                            "Add" -> MatrixOperations.addMatrices(m1, m2, result, r1, c1)
                            "Subtract" -> MatrixOperations.subtractMatrices(m1, m2, result, r1, c1)
                        }
                        matrixResult = result.joinToString(",").also { Log.d("MatrixCalc", "Result: $it, rows=$r1, cols=$c1") }
                        resultRows = r1
                        resultCols = c1
                    }
                } catch (e: Exception) {
                    errorMessage = "Error: ${e.message}"
                    Log.e("MatrixCalc", "Error: ${e.message}", e)
                }
            },
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Text("Calculate")
        }

        if (errorMessage.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.errorContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = errorMessage,
                    color = colorScheme.onErrorContainer,
                    style = TextStyle(fontSize = 16.sp),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        if (matrixResult.isNotEmpty() && resultRows > 0 && resultCols > 0) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.secondaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Result:",
                        color = colorScheme.onSecondaryContainer,
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    )
                    val values = matrixResult.split(",").map { it.trim().toDouble() }
                    for (i in 0 until resultRows) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "[",
                                color = colorScheme.onSecondaryContainer,
                                style = TextStyle(fontSize = 16.sp),
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            for (j in 0 until resultCols) {
                                val value = values[i * resultCols + j]
                                val formattedValue = if (value % 1.0 == 0.0) value.toInt().toString() else String.format("%.2f", value)
                                Text(
                                    text = formattedValue,
                                    color = colorScheme.onSecondaryContainer,
                                    style = TextStyle(fontSize = 16.sp),
                                    textAlign = TextAlign.End,
                                    modifier = Modifier
                                        .width(60.dp)
                                        .padding(horizontal = 4.dp)
                                )
                            }
                            Text(
                                text = "]",
                                color = colorScheme.onSecondaryContainer,
                                style = TextStyle(fontSize = 16.sp),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        ElevatedButton(
            onClick = onBackClick,
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = colorScheme.secondary,
                contentColor = colorScheme.onSecondary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Text("Back")
        }
    }
} 