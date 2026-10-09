package com.example.rentgo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rentgo.data.CarrosRepositorio
import com.example.rentgo.model.Carro
import com.example.rentgo.model.Categoria
import com.example.rentgo.model.formatarPreco
import com.example.rentgo.ui.theme.RentGoCores

private val listaDeFiltros = listOf("Sedã", "SUV", "Elétrico", "Até R$ 150")

/** Aplica a busca por nome e os filtros marcados (categorias + faixa de preço). */
private fun filtrarCarros(carros: List<Carro>, busca: String, filtros: List<Boolean>): List<Carro> {
    val categoriasMarcadas = buildList {
        if (filtros[0]) add(Categoria.SEDA)
        if (filtros[1]) add(Categoria.SUV)
        if (filtros[2]) add(Categoria.ELETRICO)
    }
    return carros.filter { carro ->
        (busca.isBlank() || carro.nome.contains(busca.trim(), ignoreCase = true)) &&
            (categoriasMarcadas.isEmpty() || carro.categoria in categoriasMarcadas) &&
            (!filtros[3] || carro.precoDia <= 150.0)
    }
}

/**
 * Tela de pesquisa de carros (aba "Carros"). Lê a lista do CarrosRepositorio (mutableStateListOf),
 * mostra com LazyColumn + Card e permite ADICIONAR (botão "Adicionar carro") e REMOVER (ícone de lixeira).
 * Clicar no card abre o Detalhe do carro.
 */
@Composable
fun TelaPesquisa(
    onVoltar: () -> Unit,
    onAbrirCarro: (Carro) -> Unit
) {
    var busca by remember { mutableStateOf("") }
    var mostrarDialogo by remember { mutableStateOf(false) }

    val filtrosSelecionados = remember { mutableStateListOf(false, false, false, false) }
    val carros = filtrarCarros(CarrosRepositorio.carros, busca, filtrosSelecionados)

    if (mostrarDialogo) {
        DialogoNovoCarro(
            onFechar = { mostrarDialogo = false },
            onSalvar = { nome, categoria, preco ->
                CarrosRepositorio.adicionar(nome, categoria, preco)
                mostrarDialogo = false
            }
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        color = RentGoCores.Fundo
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            TopoPesquisa(
                onVoltar = onVoltar,
                onLimpar = {
                    busca = ""
                    for (i in filtrosSelecionados.indices) filtrosSelecionados[i] = false
                }
            )

            LinhaLocalidadeEData(
                localidade = busca,
                aoMudarLocalidade = { busca = it }
            )

            LinhaDeFiltros(
                filtros = listaDeFiltros,
                selecionados = filtrosSelecionados,
                aoClicarFiltro = { indice ->
                    filtrosSelecionados[indice] = !filtrosSelecionados[indice]
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${carros.size} veículos encontrados",
                    fontSize = 13.sp,
                    color = RentGoCores.TextoCinza,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { mostrarDialogo = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RentGoCores.Azul,
                        contentColor = RentGoCores.Branco
                    )
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Adicionar carro", fontSize = 13.sp)
                }
            }

            if (carros.isEmpty()) {
                Text(
                    text = "Nenhum carro encontrado. Limpe os filtros (↻) ou adicione um carro.",
                    fontSize = 14.sp,
                    color = RentGoCores.TextoCinza,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                items(carros, key = { it.id }) { carro ->
                    CardDoCarro(
                        carro = carro,
                        aoAbrir = { onAbrirCarro(carro) },
                        aoRemover = { CarrosRepositorio.remover(carro.id) }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

/** Formulário "Novo carro": nome + preço (campo numérico) + categoria. */
@Composable
private fun DialogoNovoCarro(
    onFechar: () -> Unit,
    onSalvar: (String, Categoria, Double) -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(Categoria.SEDA) }
    var erro by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onFechar,
        title = { Text("Novo carro", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it; erro = null },
                    label = { Text("Nome do carro") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = preco,
                    onValueChange = { preco = it; erro = null },
                    label = { Text("Preço por dia (R$)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Categoria.values().forEach { opcao ->
                        val marcada = opcao == categoria
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (marcada) RentGoCores.Azul else RentGoCores.AzulClaro)
                                .clickable { categoria = opcao }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = opcao.rotulo,
                                fontSize = 13.sp,
                                color = if (marcada) RentGoCores.Branco else RentGoCores.Azul
                            )
                        }
                    }
                }
                erro?.let {
                    Spacer(Modifier.height(10.dp))
                    Text(it, color = RentGoCores.Vermelho, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val valor = preco.replace(',', '.').toDoubleOrNull()
                when {
                    nome.isBlank() -> erro = "Informe o nome do carro."
                    valor == null || valor <= 0.0 -> erro = "Informe um preço válido."
                    else -> onSalvar(nome, categoria, valor)
                }
            }) { Text("Adicionar") }
        },
        dismissButton = {
            TextButton(onClick = onFechar) { Text("Cancelar") }
        }
    )
}

@Composable
fun TopoPesquisa(onVoltar: () -> Unit, onLimpar: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(RentGoCores.Branco)
                .clickable(onClick = onVoltar),
            contentAlignment = Alignment.Center
        ) {
            Text("‹", fontSize = 22.sp, color = RentGoCores.TextoEscuro)
        }

        Text(
            text = "Pesquisa",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = RentGoCores.TextoEscuro,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(RentGoCores.Branco)
                .clickable(onClick = onLimpar),
            contentAlignment = Alignment.Center
        ) {
            Text("↻", fontSize = 20.sp, color = RentGoCores.Azul)
        }
    }
}

@Composable
fun LinhaLocalidadeEData(localidade: String, aoMudarLocalidade: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = localidade,
            onValueChange = aoMudarLocalidade,
            placeholder = { Text("Buscar carro pelo nome", color = RentGoCores.TextoCinza) },
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .width(150.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(RentGoCores.Branco)
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text("15 Out - 20 Out", color = RentGoCores.TextoEscuro, fontSize = 14.sp)
        }
    }
}

@Composable
fun LinhaDeFiltros(
    filtros: List<String>,
    selecionados: List<Boolean>,
    aoClicarFiltro: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        filtros.forEachIndexed { indice, nome ->
            val estaSelecionado = selecionados[indice]

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (estaSelecionado) RentGoCores.Azul else RentGoCores.Branco)
                    .then(
                        if (!estaSelecionado)
                            Modifier.border(1.dp, RentGoCores.Borda, RoundedCornerShape(18.dp))
                        else Modifier
                    )
                    .clickable { aoClicarFiltro(indice) }
                    .padding(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Text(
                    text = nome,
                    fontSize = 13.sp,
                    color = if (estaSelecionado) RentGoCores.Branco else RentGoCores.TextoEscuro
                )
            }

            Spacer(modifier = Modifier.width(10.dp))
        }
    }
}

@Composable
fun CardDoCarro(
    carro: Carro,
    aoAbrir: () -> Unit,
    aoRemover: () -> Unit,
    iconeAcao: ImageVector = Icons.Outlined.Delete,
    descricaoAcao: String = "Remover carro"
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = aoAbrir),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RentGoCores.Branco),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = carro.nome,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = RentGoCores.TextoEscuro
                )
                Text(
                    text = carro.categoria.rotulo,
                    fontSize = 12.sp,
                    color = RentGoCores.TextoCinza
                )

                Row(
                    modifier = Modifier.padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("★", fontSize = 13.sp, color = RentGoCores.Azul)
                    Text(" ${carro.nota}", fontSize = 13.sp, color = RentGoCores.TextoEscuro)
                }

                Text(
                    text = "${formatarPreco(carro.precoDia)} / dia",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = RentGoCores.TextoEscuro
                )
            }

            IconButton(onClick = aoRemover) {
                Icon(
                    imageVector = iconeAcao,
                    contentDescription = descricaoAcao,
                    tint = RentGoCores.Vermelho
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TelaPesquisaPreview() {
    TelaPesquisa(onVoltar = {}, onAbrirCarro = {})
}
