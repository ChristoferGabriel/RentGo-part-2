package com.example.rentgo.data

import androidx.compose.runtime.mutableStateListOf
import com.example.rentgo.model.Carro
import com.example.rentgo.model.Reserva

/**
 * Lista reativa de reservas do usuário (aba Agendamento).
 * Fica só na memória: ao fechar o app, as reservas somem.
 */
object ReservasRepositorio {

    val reservas = mutableStateListOf<Reserva>()

    private var proximoId = 1

    fun adicionar(carro: Carro, dias: Int): Reserva {
        val nova = Reserva(
            id = proximoId++,
            carroId = carro.id,
            nomeCarro = carro.nome,
            precoDia = carro.precoDia,
            dias = dias
        )
        reservas.add(nova)
        return nova
    }

    fun remover(id: Int) {
        reservas.removeAll { it.id == id }
    }

    fun alterarDias(id: Int, dias: Int) {
        val indice = reservas.indexOfFirst { it.id == id }
        if (indice != -1) reservas[indice] = reservas[indice].copy(dias = dias)
    }

    fun buscarPorId(id: Int): Reserva? = reservas.find { it.id == id }
}
