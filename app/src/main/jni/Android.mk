LOCAL_PATH := $(call my-dir)

# Stasm
include $(CLEAR_VARS)
OPENCV_LIB_TYPE:=STATIC
OPENCV_INSTALL_MODULES:=on
OPENCV_CAMERA_MODULES:=on

include /Users/arturdumchev/AndroidStudioProjects/res/OpenCV-android-sdk/sdk/native/jni/OpenCV.mk

LOCAL_MODULE    := stasm
FILE_LIST := $(wildcard $(LOCAL_PATH)/stasm/*.cpp) \
 $(wildcard $(LOCAL_PATH)/stasm/MOD_1/*.cpp) \
 $(wildcard $(LOCAL_PATH)/jnipart/jni_stasm.cpp)

LOCAL_SRC_FILES := $(FILE_LIST:$(LOCAL_PATH)/%=%)
LOCAL_LDLIBS +=  -llog -ldl
include $(BUILD_SHARED_LIBRARY)