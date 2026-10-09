package com.example.rentgo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rentgo.data.CarrosRepositorio
import com.example.rentgo.data.ReservasRepositorio
import com.example.rentgo.model.formatarPreco
import com.example.rentgo.ui.components.MAX_DIARIAS
import com.example.rentgo.ui.components.MIN_DIARIAS
import com.example.rentgo.ui.components.SeletorDiarias
import com.example.rentgo.ui.components.TopoRentGo
import com.example.rentgo.ui.theme.RentGoCores

/**
 * Detalhe da reserva. Recebe só o id pela rota e busca a reserva certa no ReservasRepositorio.
 *
 * O que ela faz a mais do que só reexibir os campos (seção 3.2 do trabalho):
 *  1. EDITA a reserva ali mesmo: os botões − / + mudam as diárias e o total é recalculado na hora;
 *  2. COMBINA as duas listas: busca o Carro da reserva em CarrosRepositorio (categoria e nota);
 *  3. Permite cancelar a reserva e voltar para a lista.
 */
@Composable
fun TelaDetalheReserva(reservaId: Int, onVoltar: () -> Unit) {
    val reserva = ReservasRepositorio.buscarPorId(reservaId)
    val carro = reserva?.let { CarrosRepositorio.buscarPorId(it.carroId) }

    Scaffold(
        containerColor = RentGoCores.Fundo,
        topBar = { TopoRentGo(titulo = "Detalhe da reserva", onVoltar = onVoltar) }
    ) { paddingInterno ->
        if (reserva == null) {
            Text(
                text = "Reserva não encontrada (talvez já tenha sido cancelada).",
                color = RentGoCores.TextoCinza,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingInterno)
                    .padding(32.dp)
            )
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = RentGoCores.Branco),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = reserva.nomeCarro,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = RentGoCores.TextoEscuro
                    )
                    Spacer(Modifier.height(6.dp))
                    // dados que vêm da outra lista (carros)
                    if (carro != null) {
                        Text("${carro.categoria.rotulo} · ★ ${carro.nota}", fontSize = 14.sp, color = RentGoCores.TextoCinza)
                    } else {
                        Text("Esse carro foi removido da lista de carros.", fontSize = 14.sp, color = RentGoCores.TextoCinza)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "${formatarPreco(reserva.precoDia)} / dia",
                        fontSize = 15.sp,
                        color = RentGoCores.TextoEscuro
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = RentGoCores.Branco),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Editar diárias", fontWeight = FontWeight.Bold, color = RentGoCores.TextoEscuro)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        SeletorDiarias(
                            dias = reserva.dias,
                            onMudar = {
                                ReservasRepositorio.alterarDias(reserva.id, it.coerceIn(MIN_DIARIAS, MAX_DIARIAS))
                            }
                        )
                    }
                    Text(
                        text = "Total: ${formatarPreco(arredondar(reserva.total))}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = RentGoCores.Azul
                    )
                    Text(
                        text = "${reserva.dias} × ${formatarPreco(reserva.precoDia)}",
                        fontSize = 13.sp,
                        color = RentGoCores.TextoCinza
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            OutlinedButton(
                onClick = {
                    ReservasRepositorio.remover(reserva.id)
                    onVoltar()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, RentGoCores.Vermelho),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RentGoCores.Vermelho)
            ) {
                Text("Cancelar reserva", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
