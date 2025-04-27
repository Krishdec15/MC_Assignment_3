#ifndef MATRIX_CALCULATOR_H
#define MATRIX_CALCULATOR_H

#include <jni.h>

extern "C" {
JNIEXPORT void JNICALL Java_com_example_matrixcalculator_matrix_MatrixOperations_addMatrices(JNIEnv* env, jobject obj, jdoubleArray matrix1, jdoubleArray matrix2, jdoubleArray result, jint rows, jint cols);
JNIEXPORT void JNICALL Java_com_example_matrixcalculator_matrix_MatrixOperations_subtractMatrices(JNIEnv* env, jobject obj, jdoubleArray matrix1, jdoubleArray matrix2, jdoubleArray result, jint rows, jint cols);
JNIEXPORT void JNICALL Java_com_example_matrixcalculator_matrix_MatrixOperations_multiplyMatrices(JNIEnv* env, jobject obj, jdoubleArray matrix1, jdoubleArray matrix2, jdoubleArray result, jint rows1, jint cols1, jint rows2, jint cols2);
JNIEXPORT void JNICALL Java_com_example_matrixcalculator_matrix_MatrixOperations_divideMatrices(JNIEnv* env, jobject obj, jdoubleArray matrix1, jdoubleArray matrix2, jdoubleArray result, jint rows1, jint cols1, jint rows2, jint cols2);
}

#endif