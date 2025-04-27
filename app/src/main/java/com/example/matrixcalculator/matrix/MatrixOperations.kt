package com.example.matrixcalculator.matrix

object MatrixOperations {
    // Native method declarations
    external fun addMatrices(matrix1: DoubleArray, matrix2: DoubleArray, result: DoubleArray, rows: Int, cols: Int)
    external fun subtractMatrices(matrix1: DoubleArray, matrix2: DoubleArray, result: DoubleArray, rows: Int, cols: Int)
    external fun multiplyMatrices(matrix1: DoubleArray, matrix2: DoubleArray, result: DoubleArray, rows1: Int, cols1: Int, rows2: Int, cols2: Int)
    external fun divideMatrices(matrix1: DoubleArray, matrix2: DoubleArray, result: DoubleArray, rows1: Int, cols1: Int, rows2: Int, cols2: Int)
} 