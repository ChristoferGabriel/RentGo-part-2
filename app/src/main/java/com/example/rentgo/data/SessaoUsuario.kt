package com.example.rentgo.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.rentgo.model.Usuario

/**
 * Guarda as contas criadas e quem está logado agora.
 *
 * Fica só na memória (mutableStateListOf / mutableStateOf), como o trabalho pede:
 * se o app for fechado, as contas somem.
 *
 * Como usuarioLogado é um estado do Compose, qualquer tela que leia
 * SessaoUsuario.usuarioLogado se atualiza sozinha quando alguém entra ou sai.
 */
object SessaoUsuario {

    val usuarios = mutableStateListOf<Usuario>()

    var usuarioLogado by mutableStateOf<Usuario?>(null)
        private set

    /** Cria a conta e já deixa o usuário logado. Devolve uma mensagem de erro, ou null se deu certo. */
    fun cadastrar(nome: String, email: String, senha: String): String? {
        val emailLimpo = email.trim().lowercase()
        if (usuarios.any { it.email == emailLimpo }) {
            return "Já existe uma conta com este e-mail."
        }
        val novo = Usuario(nome = nome.trim(), email = emailLimpo, senha = senha)
        usuarios.add(novo)
        usuarioLogado = novo
        return null
    }

    /** Confere e-mail e senha. Se baterem, esse usuário passa a ser o logado. */
    fun entrar(email: String, senha: String): Boolean {
        val encontrado = usuarios.find {
            it.email == email.trim().lowercase() && it.senha == senha
        } ?: return false
        usuarioLogado = encontrado
        return true
    }

    /** Troca a senha de uma conta existente (usado no "Recuperar senha?"). */
    fun redefinirSenha(email: String, novaSenha: String): Boolean {
        val indice = usuarios.indexOfFirst { it.email == email.trim().lowercase() }
        if (indice == -1) return false
        usuarios[indice] = usuarios[indice].copy(senha = novaSenha)
        return true
    }

    fun sair() {
        usuarioLogado = null
    }
}
