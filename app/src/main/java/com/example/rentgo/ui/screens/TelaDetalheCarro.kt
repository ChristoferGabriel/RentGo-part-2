package com.example.rentgo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rentgo.data.CarrosRepositorio
import com.example.rentgo.model.Carro
import com.example.rentgo.model.formatarPreco
import com.example.rentgo.ui.components.MAX_DIARIAS
import com.example.rentgo.ui.components.MIN_DIARIAS
import com.example.rentgo.ui.components.SeletorDiarias
import com.example.rentgo.ui.components.TopoRentGo
import com.example.rentgo.ui.theme.RentGoCores

/**
 * Detalhe do carro. Recebe só o id pela rota e busca o carro certo no CarrosRepositorio.
 *
 * O que ela faz a mais do que só reexibir os campos (seção 3.2 do trabalho):
 *  1. CALCULA o valor total da locação (diária x nº de diárias escolhido) e o preço no Pix;
 *  2. COMBINA as duas listas: mostra "Carros parecidos" (mesma categoria) e, ao tocar, abre o
 *     detalhe de outro carro (navegação secundária);
 *  3. Permite favoritar (coração na TopAppBar) e RESERVAR, que leva ao Pagamento com os dias escolhidos.
 */
@Composable
fun TelaDetalheCarro(
    carroId: Int,
    onVoltar: () -> Unit,
    onAbrirCarro: (Int) -> Unit,
    onReservar: (Carro, Int) -> Unit
) {
    val carro = CarrosRepositorio.buscarPorId(carroId)
    var dias by remember(carroId) { mutableIntStateOf(5) }

    Scaffold(
        containerColor = RentGoCores.Fundo,
        topBar = {
            TopoRentGo(
                titulo = carro?.nome ?: "Carro não encontrado",
                onVoltar = onVoltar,
                acoes = {
                    if (carro != null) {
                        val favorito = CarrosRepositorio.ehFavorito(carro.id)
                        IconButton(onClick = { CarrosRepositorio.alternarFavorito(carro.id) }) {
                            Icon(
                                imageVector = if (favorito) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (favorito) "Remover dos favoritos" else "Favoritar",
                                tint = if (favorito) RentGoCores.Vermelho else RentGoCores.TextoEscuro
                            )
                        }
                    }
                }
            )
        }
    ) { paddingInterno ->
        if (carro == null) {
            Text(
                text = "Esse carro não existe mais (foi removido da lista).",
                color = RentGoCores.TextoCinza,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingInterno)
                    .padding(32.dp)
            )
            return@Scaffold
        }

        val total = carro.precoDia * dias
        val totalPix = total * 0.95
        val parecidos = CarrosRepositorio.carros.filter {
            it.categoria == carro.categoria && it.id != carro.id
        }.take(3)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // ---- dados do carro ----
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = RentGoCores.Branco),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(carro.nome, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = RentGoCores.TextoEscuro)
                    Spacer(Modifier.height(4.dp))
                    Text(carro.categoria.rotulo, fontSize = 14.sp, color = RentGoCores.TextoCinza)
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("★ ${carro.nota}", fontSize = 15.sp, color = RentGoCores.Azul)
                        if (carro.emOferta) {
                            Spacer(Modifier.padding(horizontal = 6.dp))
                            Text(
                                text = "OFERTA DA SEMANA",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RentGoCores.Verde
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "${formatarPreco(carro.precoDia)} / dia",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = RentGoCores.TextoEscuro
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ---- cálculo do valor total ----
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = RentGoCores.Branco),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Quantas diárias?", fontWeight = FontWeight.Bold, color = RentGoCores.TextoEscuro)
                    Text(
                        "De $MIN_DIARIAS a $MAX_DIARIAS diárias",
                        fontSize = 12.sp,
                        color = RentGoCores.TextoCinza
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        SeletorDiarias(dias = dias, onMudar = { dias = it.coerceIn(MIN_DIARIAS, MAX_DIARIAS) })
                    }
                    Text(
                        text = "Total: ${formatarPreco(total)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = RentGoCores.Azul
                    )
                    Text(
                        text = "Pagando no Pix (5% de desconto): ${formatarPreco(arredondar(totalPix))}",
                        fontSize = 13.sp,
                        color = RentGoCores.TextoCinza
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { onReservar(carro, dias) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RentGoCores.Azul,
                    contentColor = RentGoCores.Branco
                )
            ) {
                Text("RESERVAR E PAGAR", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            // ---- carros parecidos (combina com a lista de carros) ----
            if (parecidos.isNotEmpty()) {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Outros ${carro.categoria.rotulo}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = RentGoCores.TextoEscuro
                )
                Spacer(Modifier.height(10.dp))
                parecidos.forEach { outro ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(RentGoCores.Branco)
                            .clickable { onAbrirCarro(outro.id) }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = outro.nome,
                            fontWeight = FontWeight.SemiBold,
                            color = RentGoCores.TextoEscuro,
                            modifier = Modifier.weight(1f)
                        )
                        Text("${formatarPreco(outro.precoDia)}/dia", color = RentGoCores.TextoCinza, fontSize = 13.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

/** Arredonda para centavos (evita "R$ 570,0000001"). */
internal fun arredondar(valor: Double): Double = Math.round(valor * 100) / 100.0
