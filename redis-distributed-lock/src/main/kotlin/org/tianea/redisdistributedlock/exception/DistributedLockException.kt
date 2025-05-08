package org.tianea.redisdistributedlock.exception

/**
 * 분산락 관련 예외 클래스
 */
class DistributedLockException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable?) : super(message, cause)
}
