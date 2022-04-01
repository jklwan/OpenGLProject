# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# glm
# include_directories(glm)
add_subdirectory(glm)
#list(APPEND LINK_LIBRARIES glm_shared)

# 搜索当前目录下的所有.cpp文件
aux_source_directory(. src_list)
aux_source_directory(./window window_list)
aux_source_directory(./utils utils_lit)

add_library(opengl_jni
        SHARED
        ${src_list}
        ${window_list}
        ${utils_lit}
        )

#find_library(log-lib log)
#find_library(dl-lib dl)
#find_library(android-lib android)

target_link_libraries(opengl_jni
        android
        log
        dl
        EGL
        GLESv2
        OpenSLES
        glm)