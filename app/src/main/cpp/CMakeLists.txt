cmake_minimum_required(VERSION 3.22.1)

project("phonelookup")

add_library(${CMAKE_PROJECT_NAME} SHARED
    native-lib.cpp)

target_compile_options(${CMAKE_PROJECT_NAME} PRIVATE
    -O3
    -ffast-math
    -funroll-loops
    -fomit-frame-pointer)

find_library(log-lib log)

target_link_libraries(${CMAKE_PROJECT_NAME}
    ${log-lib})
