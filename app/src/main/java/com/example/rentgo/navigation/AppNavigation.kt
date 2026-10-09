package com.example.rentgo.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rentgo.data.CarrosRepositorio
import com.example.rentgo.data.ReservasRepositorio
import com.example.rentgo.data.SessaoUsuario
import com.example.rentgo.ui.components.BarraInferior
import com.example.rentgo.ui.screens.CadastroScreen
import com.example.rentgo.ui.screens.HomeScreen
import com.example.rentgo.ui.screens.LoginScreen
import com.example.rentgo.ui.screens.PagamentoViewModel
import com.example.rentgo.ui.screens.PerfilScreen
import com.example.rentgo.ui.screens.TelaAgendamento
import com.example.rentgo.ui.screens.TelaDetalheCarro
import com.example.rentgo.ui.screens.TelaDetalheReserva
import com.example.rentgo.ui.screens.TelaFavoritos
import com.example.rentgo.ui.screens.TelaPagamento
import com.example.rentgo.ui.screens.TelaPesquisa
import com.example.rentgo.ui.screens.TelaReservaConcluida
import com.example.rentgo.ui.theme.RentGoCores

/** Rotas em que a barra inferior aparece. Login, cadastro e detalhes ficam sem ela. */
private val rotasComBarra = setOf(
    Rotas.HOME, Rotas.CARROS, Rotas.AGENDAMENTO, Rotas.FAVORITOS, Rotas.PERFIL
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val entradaAtual by navController.currentBackStackEntryAsState()
    val rotaAtual = entradaAtual?.destination?.route

    // Criado aqui (fora do NavHost) para ser o MESMO nas telas de Pagamento e Reserva Concluída.
    val pagamentoViewModel: PagamentoViewModel = viewModel()

    /** Troca de aba sem empilhar várias cópias da mesma tela. */
    fun irParaAba(rota: String) {
        navController.navigate(rota) {
            popUpTo(Rotas.HOME) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        containerColor = RentGoCores.Fundo,
        // cada tela cuida da barra de status; aqui o padding é só o da barra inferior
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (rotaAtual in rotasComBarra) {
                BarraInferior(rotaAtual = rotaAtual, onNavegar = { irParaAba(it) })
            }
        }
    ) { paddingInterno ->
        NavHost(
            navController = navController,
            startDestination = Rotas.LOGIN,
            modifier = Modifier.padding(paddingInterno)
        ) {
            composable(Rotas.LOGIN) {
                LoginScreen(
                    onEntrar = {
                        navController.navigate(Rotas.HOME) {
                            popUpTo(Rotas.LOGIN) { inclusive = true } // voltar não retorna ao login
                        }
                    },
                    onCriarConta = { navController.navigate(Rotas.CADASTRO) }
                )
            }

            composable(Rotas.CADASTRO) {
                CadastroScreen(
                    onVoltar = { navController.popBackStack() },
                    onCadastrado = {
                        navController.navigate(Rotas.HOME) {
                            popUpTo(Rotas.LOGIN) { inclusive = true }
                        }
                    }
                )
            }

            composable(Rotas.HOME) {
                HomeScreen(
                    onAbrirCarro = { id -> navController.navigate(Rotas.detalheCarro(id)) },
                    onVerTodas = { irParaAba(Rotas.CARROS) },
                    onAbrirPerfil = { irParaAba(Rotas.PERFIL) }
                )
            }

            composable(Rotas.PERFIL) {
                PerfilScreen(
                    onSair = {
                        SessaoUsuario.sair()
                        navController.navigate(Rotas.LOGIN) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }

            composable(Rotas.CARROS) {
                TelaPesquisa(
                    onVoltar = { navController.popBackStack() },
                    onAbrirCarro = { carro -> navController.navigate(Rotas.detalheCarro(carro.id)) }
                )
            }

            composable(Rotas.PAGAMENTO) {
                TelaPagamento(
                    viewModel = pagamentoViewModel,
                    onVoltar = { navController.popBackStack() },
                    onPagamentoConfirmado = {
                        // a reserva paga passa a aparecer na aba Agendamento
                        CarrosRepositorio.buscarPorId(pagamentoViewModel.carroId)?.let {
                            ReservasRepositorio.adicionar(it, pagamentoViewModel.dias)
                        }
                        navController.navigate(Rotas.RESERVA_CONCLUIDA) {
                            popUpTo(Rotas.PAGAMENTO) { inclusive = true } // voltar não retorna ao formulário
                        }
                    }
                )
            }

            composable(Rotas.RESERVA_CONCLUIDA) {
                TelaReservaConcluida(
                    reserva = pagamentoViewModel.reserva,
                    valorPagoCentavos = pagamentoViewModel.valorFinalCentavos,
                    onNovaReserva = {
                        pagamentoViewModel.novaReserva()
                        // volta pra Pesquisa limpando o fluxo (sem saveState, senão restauraria a tela concluída)
                        navController.navigate(Rotas.CARROS) {
                            popUpTo(Rotas.HOME)
                        }
                    }
                )
            }

            composable(Rotas.AGENDAMENTO) {
                TelaAgendamento(
                    onAbrirReserva = { id -> navController.navigate(Rotas.detalheReserva(id)) }
                )
            }

            composable(Rotas.FAVORITOS) {
                TelaFavoritos(
                    onAbrirCarro = { id -> navController.navigate(Rotas.detalheCarro(id)) }
                )
            }

            composable(
                route = Rotas.DETALHE_CARRO,
                arguments = listOf(navArgument(Rotas.ARG_CARRO_ID) { type = NavType.IntType })
            ) { entrada ->
                val id = entrada.arguments?.getInt(Rotas.ARG_CARRO_ID) ?: -1
                TelaDetalheCarro(
                    carroId = id,
                    onVoltar = { navController.popBackStack() },
                    onAbrirCarro = { outroId -> navController.navigate(Rotas.detalheCarro(outroId)) },
                    onReservar = { carro, dias ->
                        pagamentoViewModel.iniciarReserva(carro, dias)
                        navController.navigate(Rotas.PAGAMENTO)
                    }
                )
            }

            composable(
                route = Rotas.DETALHE_RESERVA,
                arguments = listOf(navArgument(Rotas.ARG_RESERVA_ID) { type = NavType.IntType })
            ) { entrada ->
                val id = entrada.arguments?.getInt(Rotas.ARG_RESERVA_ID) ?: -1
                TelaDetalheReserva(
                    reservaId = id,
                    onVoltar = { navController.popBackStack() }
                )
            }
        }
    }
}
