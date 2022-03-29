#include <jni.h>
#include <string>
#include <android/log.h>

extern "C" JNIEXPORT jstring JNICALL
Java_com_crestron_test_anrverification_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    char *str = NULL;
    __android_log_print(ANDROID_LOG_ERROR, "AnrVerification", "LENGTH  %d\n", strlen(str));

    return env->NewStringUTF(hello.c_str());
}
