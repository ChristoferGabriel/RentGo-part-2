package com.example.rentgo.model

/**
 * Usuário cadastrado na tela "Criar conta".
 * É daqui que sai o nome mostrado no "Olá, ..." da tela inicial.
 */
data class Usuario(
    val nome: String,
    val email: String,
    val senha: String
) {
    /** "Maria Souza" -> "Maria" (é o que aparece no "Olá, Maria 👋") */
    val primeiroNome: String
        get() = nome.trim().split(Regex("\\s+")).first()

    /** "Maria Souza" -> "MS" (vai no avatar, no lugar da foto) */
    val iniciais: String
        get() = nome.trim()
            .split(Regex("\\s+"))
            .filter { it.isNotEmpty() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
}
