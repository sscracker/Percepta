package com.percepta.app.detection

import cocoapods.onnxruntime_objc.ORTEnv
import cocoapods.onnxruntime_objc.ORTLoggingLevel
import cocoapods.onnxruntime_objc.ORTSession
import cocoapods.onnxruntime_objc.ORTTensorElementDataType
import cocoapods.onnxruntime_objc.ORTValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSMutableData
import platform.Foundation.NSNumber
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.appendBytes
import platform.Foundation.dataWithBytes
import platform.Foundation.writeToFile
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
actual class OnnxSession(
    private val env: ORTEnv,
    private val session: ORTSession,
) : AutoCloseable {

    actual fun run(input: FloatArray): FloatArray = memScoped {
        val inputData = NSMutableData()
        input.usePinned { pinned ->
            inputData.appendBytes(pinned.addressOf(0), (input.size * 4).toULong())
        }

        val shape = listOf(NSNumber(int = 1), NSNumber(int = 3), NSNumber(int = 640), NSNumber(int = 640))

        val valErr = alloc<ObjCObjectVar<NSError?>>()
        val inputValue = ORTValue(
            tensorData = inputData,
            elementType = ORTTensorElementDataType.ORTTensorElementDataTypeFloat,
            shape = shape,
            error = valErr.ptr,
        )

        val err = alloc<ObjCObjectVar<NSError?>>()
        val inputName = session.inputNamesWithError(err.ptr)?.firstOrNull() as? String
            ?: error("inputNames failed: ${err.value?.localizedDescription}")
        val outputName = session.outputNamesWithError(err.ptr)?.firstOrNull() as? String
            ?: error("outputNames failed: ${err.value?.localizedDescription}")

        val outputs = session.runWithInputs(
            inputs = mapOf(inputName to inputValue),
            outputNames = setOf(outputName),
            runOptions = null,
            error = err.ptr,
        ) ?: error("runWithInputs failed: ${err.value?.localizedDescription}")

        val outputValue = outputs[outputName] as? ORTValue
            ?: error("output '$outputName' missing; keys=${outputs.keys}")

        val outputData = outputValue.tensorDataWithError(err.ptr)
            ?: error("tensorData failed: ${err.value?.localizedDescription}")

        val byteLength = outputData.length.toInt()
        val result = FloatArray(byteLength / 4)
        result.usePinned { pinned ->
            memcpy(pinned.addressOf(0), outputData.bytes, byteLength.toULong())
        }
        result
    }

    actual override fun close() {}
}

@OptIn(ExperimentalForeignApi::class)
actual fun createOnnxSession(modelBytes: ByteArray): OnnxSession = memScoped {
    val path = NSTemporaryDirectory() + "model.onnx"
    val nsData = modelBytes.usePinned { pinned ->
        NSData.dataWithBytes(pinned.addressOf(0), modelBytes.size.toULong())
    }
    val wrote = nsData.writeToFile(path, atomically = true)
    if (!wrote) error("failed writing model to temp file (bytes=${modelBytes.size})")

    val envErr = alloc<ObjCObjectVar<NSError?>>()
    val env = try {
        ORTEnv(loggingLevel = ORTLoggingLevel.ORTLoggingLevelWarning, error = envErr.ptr)
    } catch (e: Throwable) {
        error("ORTEnv failed: ${envErr.value?.localizedDescription ?: e.message}")
    }

    val sessErr = alloc<ObjCObjectVar<NSError?>>()
    val session = try {
        ORTSession(env = env, modelPath = path, sessionOptions = null, error = sessErr.ptr)
    } catch (e: Throwable) {
        error("ORTSession failed: ${sessErr.value?.localizedDescription ?: e.message}")
    }

    OnnxSession(env, session)
}