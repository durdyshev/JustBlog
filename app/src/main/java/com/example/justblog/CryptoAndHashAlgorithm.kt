package com.example.justblog

import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class CryptAndHashAlgorithm {
    object Hash {
        fun sha256(input: String): String {
            return try {
                val md = MessageDigest.getInstance("SHA-256")
                val messageDigest = md.digest(input.toByteArray())
                val no = BigInteger(1, messageDigest)
                var hashtext = no.toString(16)
                while (hashtext.length < 32) {
                    hashtext = "0$hashtext"
                }
                hashtext
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException(e)
            }
        }

        fun md5(input: String): String {
            val md = MessageDigest.getInstance("MD5")
            return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
        }
    }
}