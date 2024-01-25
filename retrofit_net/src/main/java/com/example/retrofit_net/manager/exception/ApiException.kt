package com.example.retrofit_net.manager.exception

import java.io.IOException

open class ApiException : Exception {
    var errCode: String
    var errSubCode: String?=""
    var errMsg: String

    constructor(code: String, subCode: String?, msg: String, e: Throwable? = null) : super(e) {
        this.errCode = code
        this.errSubCode = subCode
        this.errMsg = msg
    }
}

class NoNetworkException : IOException {
    var errCode: Int
    var errMsg: String

    constructor(errCode: Int, errMsg: String, e: Throwable? = null) : super(e) {
        this.errCode = errCode
        this.errMsg = errMsg
    }
}