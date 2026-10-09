package com.example.rentgo.model

import java.text.NumberFormat
import java.util.Locale

/** Dados da reserva mostrados nas telas de Pagamento e Reserva Concluída. */
data class DadosReserva(
    val nomeCarro: String,
    val periodo: String,
    val valorCentavos: Long
)

/** 60_000 -> "R$ 600,00" */
fun formatarMoeda(centavos: Long): String {
    val formato = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
    return formato.format(centavos / 100.0)
}

/**
 * Reserva (agendamento) de um carro. É o 2º tipo de item do app, usado na lista da aba Agendamento.
 * Guarda uma "foto" do nome e do preço do carro no momento da reserva.
 */
data class Reserva(
    val id: Int,
    val carroId: Int,
    val nomeCarro: String,
    val precoDia: Double,
    val dias: Int
) {
    /** Valor calculado a partir dos dados da reserva (usado no Detalhe). */
    val total: Double
        get() = precoDia * dias
}
