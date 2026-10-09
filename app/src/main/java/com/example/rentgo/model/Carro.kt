package com.example.rentgo.model

import androidx.annotation.DrawableRes
import java.util.Locale

/** Categorias que aparecem nos cartões "Sedã / SUV / Elétrico" da Home. */
enum class Categoria(val rotulo: String) {
    SEDA("Sedã"),
    SUV("SUV"),
    ELETRICO("Elétrico")
}

data class Carro(
    val id: Int,
    val nome: String,
    val categoria: Categoria,
    val precoDia: Double,
    val nota: Double,
    val emOferta: Boolean = false,
    /** Foto do carro em res/drawable (ex: R.drawable.ford_focus). Se for null, a Home mostra um ícone. */
    @DrawableRes val imagemRes: Int? = null
)

/** 120.0 -> "R$ 120"   |   99.9 -> "R$ 99,90" */
fun formatarPreco(valor: Double): String =
    if (valor % 1.0 == 0.0) "R$ ${valor.toInt()}"
    else "R$ " + String.format(Locale.forLanguageTag("pt-BR"), "%.2f", valor)
