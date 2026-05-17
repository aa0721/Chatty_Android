package com.example.chatty_android.common.utils

import com.example.chatty_android.common.constants.Constants
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object EncryptionUtils {

    fun encryptAes(plainText: String, secretKey: SecretKey): Pair<ByteArray, ByteArray> {
        val cipher = Cipher.getInstance(Constants.AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return Pair(iv, encrypted)
    }

    fun decryptAes(iv: ByteArray, encryptedData: ByteArray, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance(Constants.AES_MODE)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        val decrypted = cipher.doFinal(encryptedData)
        return String(decrypted, Charsets.UTF_8)
    }

    fun getOrCreateAesKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        val existingKey = keyStore.getEntry(Constants.KEY_STORE_ALIAS, null) as? KeyStore.SecretKeyEntry
        if (existingKey != null) {
            return existingKey.secretKey
        }

        val keyGenerator = KeyGenerator.getInstance(
            android.security.keystore.KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )
        val spec = android.security.keystore.KeyGenParameterSpec.Builder(
            Constants.KEY_STORE_ALIAS,
            android.security.keystore.KeyProperties.PURPOSE_ENCRYPT or
                android.security.keystore.KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(android.security.keystore.KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    fun hashPassword(password: String): String {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }
}
