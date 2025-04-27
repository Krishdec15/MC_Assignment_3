

cmake_minimum_required(VERSION 3.22.1)

project("matrixcalculator")

add_library(
        matrixcalculator
        SHARED
        matrix_calculator.cpp
)

# Add Eigen include path
include_directories(${CMAKE_SOURCE_DIR}/eigen)

find_library(log-lib log)

target_link_libraries(
        matrixcalculator
        ${log-lib}
)