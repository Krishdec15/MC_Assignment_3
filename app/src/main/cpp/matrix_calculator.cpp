#include <jni.h>
#include "Eigen/Dense"
#include "matrix_calculator.h"
#include <android/log.h>

using Eigen::MatrixXd;

extern "C" {
JNIEXPORT void JNICALL Java_com_example_matrixcalculator_matrix_MatrixOperations_addMatrices(
        JNIEnv* env, jobject obj, jdoubleArray matrix1, jdoubleArray matrix2, jdoubleArray result, jint rows, jint cols) {
    jdouble* m1 = env->GetDoubleArrayElements(matrix1, nullptr);
    jdouble* m2 = env->GetDoubleArrayElements(matrix2, nullptr);
    jdouble* res = env->GetDoubleArrayElements(result, nullptr);

    MatrixXd mat1 = Eigen::Map<MatrixXd>(m1, rows, cols);
    MatrixXd mat2 = Eigen::Map<MatrixXd>(m2, rows, cols);
    MatrixXd res_mat = MatrixXd(rows, cols);

    res_mat = mat1 + mat2;

    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            res[i * cols + j] = res_mat(i, j);
        }
    }

    env->ReleaseDoubleArrayElements(matrix1, m1, 0);
    env->ReleaseDoubleArrayElements(matrix2, m2, 0);
    env->ReleaseDoubleArrayElements(result, res, 0);
}

JNIEXPORT void JNICALL Java_com_example_matrixcalculator_matrix_MatrixOperations_subtractMatrices(
        JNIEnv* env, jobject obj, jdoubleArray matrix1, jdoubleArray matrix2, jdoubleArray result, jint rows, jint cols) {
    jdouble* m1 = env->GetDoubleArrayElements(matrix1, nullptr);
    jdouble* m2 = env->GetDoubleArrayElements(matrix2, nullptr);
    jdouble* res = env->GetDoubleArrayElements(result, nullptr);

    MatrixXd mat1 = Eigen::Map<MatrixXd>(m1, rows, cols);
    MatrixXd mat2 = Eigen::Map<MatrixXd>(m2, rows, cols);
    MatrixXd res_mat = MatrixXd(rows, cols);

    res_mat = mat1 - mat2;

    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            res[i * cols + j] = res_mat(i, j);
        }
    }

    env->ReleaseDoubleArrayElements(matrix1, m1, 0);
    env->ReleaseDoubleArrayElements(matrix2, m2, 0);
    env->ReleaseDoubleArrayElements(result, res, 0);
}

JNIEXPORT void JNICALL Java_com_example_matrixcalculator_matrix_MatrixOperations_multiplyMatrices(
        JNIEnv* env, jobject obj, jdoubleArray matrix1, jdoubleArray matrix2, jdoubleArray result,
        jint rows1, jint cols1, jint rows2, jint cols2) {
    if (cols1 != rows2) {
        jclass exceptionClass = env->FindClass("java/lang/IllegalArgumentException");
        env->ThrowNew(exceptionClass, "Matrix dimensions incompatible: cols1 must equal rows2");
        return;
    }

    jdouble* m1 = env->GetDoubleArrayElements(matrix1, nullptr);
    jdouble* m2 = env->GetDoubleArrayElements(matrix2, nullptr);
    jdouble* res = env->GetDoubleArrayElements(result, nullptr);

    // Log raw input data
    __android_log_print(ANDROID_LOG_DEBUG, "MatrixCalc", "Multiply: rows1=%d, cols1=%d, rows2=%d, cols2=%d", rows1, cols1, rows2, cols2);
    for (int i = 0; i < rows1 * cols1; i++) {
        __android_log_print(ANDROID_LOG_DEBUG, "MatrixCalc", "m1 raw[%d]: %f", i, m1[i]);
    }
    for (int i = 0; i < rows2 * cols2; i++) {
        __android_log_print(ANDROID_LOG_DEBUG, "MatrixCalc", "m2 raw[%d]: %f", i, m2[i]);
    }

    // Explicitly map row-major
    MatrixXd mat1(rows1, cols1);
    MatrixXd mat2(rows2, cols2);
    for (int i = 0; i < rows1; i++) {
        for (int j = 0; j < cols1; j++) {
            mat1(i, j) = m1[i * cols1 + j];
        }
    }
    for (int i = 0; i < rows2; i++) {
        for (int j = 0; j < cols2; j++) {
            mat2(i, j) = m2[i * cols2 + j];
        }
    }

    // Log matrices as interpreted
    for (int i = 0; i < rows1; i++) {
        for (int j = 0; j < cols1; j++) {
            __android_log_print(ANDROID_LOG_DEBUG, "MatrixCalc", "mat1(%d,%d): %f", i, j, mat1(i, j));
        }
    }
    for (int i = 0; i < rows2; i++) {
        for (int j = 0; j < cols2; j++) {
            __android_log_print(ANDROID_LOG_DEBUG, "MatrixCalc", "mat2(%d,%d): %f", i, j, mat2(i, j));
        }
    }

    MatrixXd res_mat = mat1 * mat2;

    // Log result matrix
    for (int i = 0; i < rows1; i++) {
        for (int j = 0; j < cols2; j++) {
            __android_log_print(ANDROID_LOG_DEBUG, "MatrixCalc", "Result(%d,%d): %f", i, j, res_mat(i, j));
        }
    }

    for (int i = 0; i < rows1; i++) {
        for (int j = 0; j < cols2; j++) {
            res[i * cols2 + j] = res_mat(i, j);
        }
    }

    env->ReleaseDoubleArrayElements(matrix1, m1, 0);
    env->ReleaseDoubleArrayElements(matrix2, m2, 0);
    env->ReleaseDoubleArrayElements(result, res, 0);
}

JNIEXPORT void JNICALL Java_com_example_matrixcalculator_matrix_MatrixOperations_divideMatrices(
        JNIEnv* env, jobject obj, jdoubleArray matrix1, jdoubleArray matrix2, jdoubleArray result,
        jint rows1, jint cols1, jint rows2, jint cols2) {
    if (cols1 != rows2) {
        jclass exceptionClass = env->FindClass("java/lang/IllegalArgumentException");
        env->ThrowNew(exceptionClass, "Matrix dimensions incompatible: cols1 must equal rows2");
        return;
    }
    if (rows2 != cols2) {
        jclass exceptionClass = env->FindClass("java/lang/IllegalArgumentException");
        env->ThrowNew(exceptionClass, "Matrix 2 must be square for division");
        return;
    }

    jdouble* m1 = env->GetDoubleArrayElements(matrix1, nullptr);
    jdouble* m2 = env->GetDoubleArrayElements(matrix2, nullptr);
    jdouble* res = env->GetDoubleArrayElements(result, nullptr);

    // Log raw input data and dimensions
    __android_log_print(ANDROID_LOG_DEBUG, "MatrixCalc", "Divide: rows1=%d, cols1=%d, rows2=%d, cols2=%d", rows1, cols1, rows2, cols2);
    for (int i = 0; i < rows1 * cols1; i++) {
        __android_log_print(ANDROID_LOG_DEBUG, "MatrixCalc", "m1 raw[%d]: %f", i, m1[i]);
    }
    for (int i = 0; i < rows2 * cols2; i++) {
        __android_log_print(ANDROID_LOG_DEBUG, "MatrixCalc", "m2 raw[%d]: %f", i, m2[i]);
    }

    // Explicitly map row-major
    MatrixXd mat1(rows1, cols1);
    MatrixXd mat2(rows2, cols2);
    for (int i = 0; i < rows1; i++) {
        for (int j = 0; j < cols1; j++) {
            mat1(i, j) = m1[i * cols1 + j];
        }
    }
    for (int i = 0; i < rows2; i++) {
        for (int j = 0; j < cols2; j++) {
            mat2(i, j) = m2[i * cols2 + j];
        }
    }

    // Log matrices as interpreted
    for (int i = 0; i < rows1; i++) {
        for (int j = 0; j < cols1; j++) {
            __android_log_print(ANDROID_LOG_DEBUG, "MatrixCalc", "mat1(%d,%d): %f", i, j, mat1(i, j));
        }
    }
    for (int i = 0; i < rows2; i++) {
        for (int j = 0; j < cols2; j++) {
            __android_log_print(ANDROID_LOG_DEBUG, "MatrixCalc", "mat2(%d,%d): %f", i, j, mat2(i, j));
        }
    }

    // Check if mat2 is invertible
    Eigen::FullPivLU<MatrixXd> lu(mat2);
    if (!lu.isInvertible()) {
        env->ReleaseDoubleArrayElements(matrix1, m1, 0);
        env->ReleaseDoubleArrayElements(matrix2, m2, 0);
        env->ReleaseDoubleArrayElements(result, res, 0);
        jclass exceptionClass = env->FindClass("java/lang/IllegalArgumentException");
        env->ThrowNew(exceptionClass, "Matrix 2 is not invertible");
        return;
    }

    MatrixXd res_mat = mat1 * mat2.inverse();

    // Log B inverse
    MatrixXd mat2_inv = mat2.inverse();
    for (int i = 0; i < rows2; i++) {
        for (int j = 0; j < cols2; j++) {
            __android_log_print(ANDROID_LOG_DEBUG, "MatrixCalc", "mat2_inv(%d,%d): %f", i, j, mat2_inv(i, j));
        }
    }

    // Log result matrix
    for (int i = 0; i < rows1; i++) {
        for (int j = 0; j < cols2; j++) {
            __android_log_print(ANDROID_LOG_DEBUG, "MatrixCalc", "Result(%d,%d): %f", i, j, res_mat(i, j));
        }
    }

    for (int i = 0; i < rows1; i++) {
        for (int j = 0; j < cols2; j++) {
            res[i * cols2 + j] = res_mat(i, j);
        }
    }

    // Log final output array
    for (int i = 0; i < rows1 * cols2; i++) {
        __android_log_print(ANDROID_LOG_DEBUG, "MatrixCalc", "res raw[%d]: %f", i, res[i]);
    }

    env->ReleaseDoubleArrayElements(matrix1, m1, 0);
    env->ReleaseDoubleArrayElements(matrix2, m2, 0);
    env->ReleaseDoubleArrayElements(result, res, 0);
}
}