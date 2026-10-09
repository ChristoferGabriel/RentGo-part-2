package com.example.rentgo.navigation

/** Todas as rotas do app, num lugar só. */
object Rotas {
    const val LOGIN = "login"
    const val CADASTRO = "cadastro"

    // abas da barra inferior
    const val HOME = "home"
    const val CARROS = "carros"
    const val AGENDAMENTO = "agendamento"
    const val FAVORITOS = "favoritos"
    const val PERFIL = "perfil"

    // detalhe recebe o id do carro pela rota: "detalhe_carro/3"
    const val ARG_CARRO_ID = "carroId"
    const val DETALHE_CARRO = "detalhe_carro/{$ARG_CARRO_ID}"

    // fluxo de reserva: Pesquisa (Carros) -> Pagamento -> Reserva concluída
    const val PAGAMENTO = "pagamento"
    const val RESERVA_CONCLUIDA = "reserva_concluida"

    // detalhe da reserva recebe o id da reserva: "detalhe_reserva/2"
    const val ARG_RESERVA_ID = "reservaId"
    const val DETALHE_RESERVA = "detalhe_reserva/{$ARG_RESERVA_ID}"

    fun detalheCarro(id: Int) = "detalhe_carro/$id"
    fun detalheReserva(id: Int) = "detalhe_reserva/$id"
}
