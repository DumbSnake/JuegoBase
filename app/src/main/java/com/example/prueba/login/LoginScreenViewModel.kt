package com.example.prueba.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prueba.model.User
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginScreenViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun signInWithGoogleCredential(credential: AuthCredential, onSuccess: () -> Unit) {
        _loading.value = true
        viewModelScope.launch {
            try {
                auth.signInWithCredential(credential).await()
                Log.d("Login", "Inicio de sesión con Google exitoso")
                onSuccess()
            } catch (e: Exception) {
                Log.e("Login", "Error al iniciar sesión con Google: ${e.message}")
                _errorMessage.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String, onSuccess: () -> Unit) {
        _loading.value = true
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                Log.d("Login", "Inicio de sesión exitoso para $email")
                onSuccess()
            } catch (e: Exception) {
                Log.e("Login", "Error al iniciar sesión: ${e.message}")
                _errorMessage.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun createUserWithEmailAndPassword(email: String, password: String, onSuccess: () -> Unit) {
        _loading.value = true
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                createUser(auth.currentUser?.displayName.orEmpty())
                Log.d("Login", "Usuario creado exitosamente")
                onSuccess()
            } catch (e: Exception) {
                Log.e("Login", "Error al crear usuario: ${e.message}")
                _errorMessage.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    private fun createUser(displayName: String) {
        val userId = auth.currentUser?.uid.orEmpty()
        val user = User(
            id = null,
            userId = userId,
            displayName = displayName,
            avatarUrl = "",
            quote = "Lo difícil ya pasó",
            profession = "Android Developer"
        ).toMap()

        FirebaseFirestore.getInstance().collection("users").add(user)
            .addOnSuccessListener { Log.d("Login", "Usuario creado en Firestore") }
            .addOnFailureListener { Log.e("Login", "Error al crear usuario en Firestore: ${it.message}") }
    }
}
