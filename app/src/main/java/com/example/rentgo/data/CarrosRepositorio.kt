package com.example.rentgo.data

import androidx.compose.runtime.mutableStateListOf
import com.example.rentgo.model.Carro
import com.example.rentgo.model.Categoria

/**
 * Lista reativa de carros do app. A Home mostra os que estão em oferta;
 * a futura tela "Carros" pode usar essa mesma lista para adicionar/remover.
 *
 * Para usar fotos de verdade: coloque a imagem em res/drawable (ex: ford_focus.jpg)
 * e troque o imagemRes, ex: imagemRes = R.drawable.ford_focus
 * (lembre do import com.example.rentgo.R).
 */
object CarrosRepositorio {

    val carros = mutableStateListOf(
        Carro(1, "Ford Focus 2023", Categoria.SEDA, 120.0, 4.9, emOferta = true),
        Carro(2, "Jeep Compass", Categoria.SUV, 180.0, 4.8, emOferta = true),
        Carro(3, "BYD Dolphin", Categoria.ELETRICO, 210.0, 4.9, emOferta = true),
        Carro(4, "Toyota Corolla", Categoria.SEDA, 150.0, 4.7, emOferta = true),
        Carro(5, "Hyundai Creta", Categoria.SUV, 165.0, 4.6),
        Carro(6, "Renault Kwid E-Tech", Categoria.ELETRICO, 140.0, 4.5)
    )

    /** Ids dos carros favoritados (aba Favoritos). */
    val favoritos = mutableStateListOf<Int>()

    fun buscarPorId(id: Int): Carro? = carros.find { it.id == id }

    fun adicionar(nome: String, categoria: Categoria, precoDia: Double): Carro {
        val novo = Carro(
            id = (carros.maxOfOrNull { it.id } ?: 0) + 1,
            nome = nome.trim(),
            categoria = categoria,
            precoDia = precoDia,
            nota = 5.0
        )
        carros.add(novo)
        return novo
    }

    fun remover(id: Int) {
        carros.removeAll { it.id == id }
        favoritos.remove(id)
    }

    fun ehFavorito(id: Int): Boolean = id in favoritos

    fun alternarFavorito(id: Int) {
        if (!favoritos.remove(id)) favoritos.add(id)
    }
}
