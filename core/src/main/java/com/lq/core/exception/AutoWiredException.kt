package com.lq.core.exception

open class AutoWiredException(
    message: String,
) : RuntimeException(message)

class PutTypeIllegalException(
    type: String,
) : AutoWiredException("Bundle type :$type is illegal")

class BundleEmptyException(
    classInfo: String,
) : AutoWiredException("Current class : $classInfo has empty bundle")
