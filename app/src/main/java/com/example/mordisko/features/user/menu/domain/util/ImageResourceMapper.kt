package com.example.mordisko.features.user.menu.domain.util

import com.example.mordisko.R

fun mapImageNameToDrawable(name: String?): Int {
    return when (name) {
        "ic_calzone2_background" -> R.drawable.ic_calzone2_background
        "ic_calzone3_background" -> R.drawable.ic_calzone3_background
        "ic_calzone4_background" -> R.drawable.ic_calzone4_background
        "ic_dedos_queso_background" -> R.drawable.ic_dedos_queso_background
        "ic_ingredientes_background" -> R.drawable.ic_ingredientes_background
        "ic_lipton_background" -> R.drawable.ic_lipton_background
        "ic_pizzas11_menu" -> R.drawable.ic_pizzas11_menu
        "ic_pizzas12_menu" -> R.drawable.ic_pizzas12_menu
        "ic_pizzas13_menu" -> R.drawable.ic_pizzas13_menu
        "ic_pizzas14_menu" -> R.drawable.ic_pizzas14_menu
        "ic_pizzas15_menu" -> R.drawable.ic_pizzas15_menu
        "ic_pizzas1_menu" -> R.drawable.ic_pizzas1_menu
        "ic_pizzas2_menu" -> R.drawable.ic_pizzas2_menu
        "ic_pizzas3_menu" -> R.drawable.ic_pizzas3_menu
        "ic_pizzas4_menu" -> R.drawable.ic_pizzas4_menu
        "ic_pizzas5_menu" -> R.drawable.ic_pizzas5_menu
        "ic_pizzas6_menu" -> R.drawable.ic_pizzas6_menu
        "ic_pizzas8_menu" -> R.drawable.ic_pizzas8_menu
        "ic_pizzas9_menu" -> R.drawable.ic_pizzas9_menu
        "ic_postres3_background" -> R.drawable.ic_postres3_background
        "ic_postres4_background" -> R.drawable.ic_postres4_background
        "ic_postres5_background" -> R.drawable.ic_postres5_background
        "ic_refresco_agua_mineral" -> R.drawable.ic_refresco_agua_mineral
        "ic_refresco_chinotto_grande" -> R.drawable.ic_refresco_chinotto_grande
        "ic_refresco_chinotto_lata" -> R.drawable.ic_refresco_chinotto_lata
        "ic_refresco_chinotto_mediano" -> R.drawable.ic_refresco_chinotto_mediano
        "ic_refresco_coca_grande" -> R.drawable.ic_refresco_coca_grande
        "ic_refresco_coca_lata" -> R.drawable.ic_refresco_coca_lata
        "ic_refresco_coca_mediana" -> R.drawable.ic_refresco_coca_mediana
        "ic_refresco_frescolita_lata" -> R.drawable.ic_refresco_frescolita_lata
        "ic_refresco_freskolita_grande" -> R.drawable.ic_refresco_freskolita_grande
        "ic_refresco_freskolita_mediana" -> R.drawable.ic_refresco_freskolita_mediana
        "ic_refresco_manzanita_grande" -> R.drawable.ic_refresco_manzanita_grande
        "ic_refresco_naranja_grande" -> R.drawable.ic_refresco_naranja_grande
        "ic_refresco_naranja_lata" -> R.drawable.ic_refresco_naranja_lata
        "ic_refresco_pepsi_grande" -> R.drawable.ic_refresco_pepsi_grande
        "ic_refresco_pepsi_lata" -> R.drawable.ic_refresco_pepsi_lata
        "ic_refresco_pepsi_mediana" -> R.drawable.ic_refresco_pepsi_mediana
        "ic_refresco_uva_grande" -> R.drawable.ic_refresco_uva_grande
        "ic_rolls_background" -> R.drawable.ic_rolls_background
        else -> R.drawable.ic_placeholder
    }
}