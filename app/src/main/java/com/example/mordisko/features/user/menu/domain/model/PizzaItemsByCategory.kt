package com.example.mordisko.features.user.menu.domain.model

import com.example.mordisko.R
import com.example.mordisko.features.user.menu.domain.util.mapStringToPizzaItemCategory

fun getPizzaItemsForCategory(category: String): List<PizzaItem> {
    val enumCategory = mapStringToPizzaItemCategory(category)

    return when (enumCategory) {
        PizzaItemCategory.PIZZAS -> listOf(
            PizzaItem("Tipo 1", "Salsa, Jamon, queso mozzarella y maiz.", R.drawable.ic_pizzas1_menu, PizzaItemCategory.PIZZAS,priceBySize = mapOf("EG" to 21.0, "Gde" to 16.50, "Med" to 13.0, "Peq" to 6.50)),
            PizzaItem("Tipo 2", "Salsa, Jamon, queso mozzarella y tocineta.", R.drawable.ic_pizzas2_menu, PizzaItemCategory.PIZZAS,priceBySize = mapOf("EG" to 22.0, "Gde" to 17.50, "Med" to 14.0, "Peq" to 7.50)),
            PizzaItem("Tipo 3", "Salsa, queso mozzarella y pepperoni.", R.drawable.ic_pizzas3_menu, PizzaItemCategory.PIZZAS,priceBySize = mapOf("EG" to 21.0, "Gde" to 16.50, "Med" to 14.0, "Peq" to 7.50)),
            PizzaItem("Tipo 4", "Salsa, queso mozzarella, tocineta y maiz.", R.drawable.ic_pizzas4_menu,
                PizzaItemCategory.PIZZAS,priceBySize = mapOf("EG" to 21.0, "Gde" to 16.50, "Med" to 13.0, "Peq" to 6.50)),
            PizzaItem("La Libre", "Salsa, queso mozzarella. Los ingredientes de tu preferencia tienen un costo adicional.", R.drawable.ic_pizzas5_menu,
                PizzaItemCategory.PIZZAS,priceBySize = mapOf("EG" to 18.50, "Gde" to 13.0, "Med" to 10.0, "Peq" to 6.0)),
            PizzaItem("Macagr Burger", "Salsa burger, topping de carne, cebolla, tomate, jamon, queso mozzarella, tocineta, queso pecorino y queso cheddar.", R.drawable.ic_pizzas6_menu, PizzaItemCategory.PIZZAS, priceBySize = mapOf("EG" to 26.0, "Gde" to 20.0, "Med" to 15.0, "Peq" to 8.0)),
            PizzaItem("Super Macagr", "Salsa, queso mozzarella, jamon, topping de cerdo, pepperoni, tocineta, pimenton, cebolla y maiz.", R.drawable.ic_pizzas12_menu, PizzaItemCategory.PIZZAS, priceBySize = mapOf("EG" to 24.0, "Gde" to 18.0, "Med" to 15.0, "Peq" to 7.50)),
            PizzaItem("Waikiki", "Salsa, piña en almibar, jamon y doble queso mozzarella.", R.drawable.ic_pizzas8_menu,
                PizzaItemCategory.PIZZAS, priceBySize = mapOf("EG" to 22.0, "Gde" to 16.0, "Med" to 13.50, "Peq" to 6.50)),
            PizzaItem("Todo Terreno", "Salsa, queso mozzarella, topping de cerdo, tocineta, topping de carne, chorizo, jamon y pepperoni.", R.drawable.ic_pizzas9_menu, PizzaItemCategory.PIZZAS, priceBySize = mapOf("EG" to 25.0, "Gde" to 20.0, "Med" to 15.0, "Peq" to 8.0)),
            PizzaItem("Full Cheese", "Salsa, tres quesos(Mozzarella, cheddar y pecorino), tomate, maiz, jamon, pepperoni y borde cubierto con parmesano.", R.drawable.ic_pizzas2_menu,
                PizzaItemCategory.PIZZAS, priceBySize = mapOf("EG" to 25.0, "Gde" to 20.0, "Med" to 15.5, "Peq" to 8.0)),
            PizzaItem("Chicken Bacon", "Salsa, pollo a la parrilla, cebolla, pimenton, aceitunas negras, tocineta y salsa BBQ.", R.drawable.ic_pizzas11_menu, PizzaItemCategory.PIZZAS,priceBySize = mapOf("EG" to 24.0, "Gde" to 17.5, "Med" to 15.5, "Peq" to 8.0)),
            PizzaItem("Caprichosa", "Salsa, tiras de chuleta ahumada, cebolla, pimenton rojo, queso mozzarella, aceitunas verdes y salchichon.", R.drawable.ic_pizzas12_menu, PizzaItemCategory.PIZZAS, priceBySize = mapOf("EG" to 27.0, "Gde" to 20.0, "Med" to 15.5, "Peq" to 8.5)),
            PizzaItem("Vegetariana", "Salsa, queso mozzarella, pimenton, cebolla, tomate y maiz.", R.drawable.ic_pizzas13_menu, PizzaItemCategory.PIZZAS, priceBySize = mapOf("EG" to 22.0, "Gde" to 16.0, "Med" to 12.50, "Peq" to 6.5)),
            PizzaItem("Bahia", "Salsa, queso mozzarella, pepperoni, piña en almibar, bordes cubiertos con parmesano.", R.drawable.ic_pizzas14_menu, PizzaItemCategory.PIZZAS,priceBySize = mapOf("EG" to 25.0, "Gde" to 18.5, "Med" to 14.0, "Peq" to 8.0)),
            PizzaItem("Favorite de Jend's", "Salsa, doble queso, doble jamon y doble pepperoni.", R.drawable.ic_pizzas15_menu, PizzaItemCategory.PIZZAS, priceBySize = mapOf("EG" to 24.0, "Gde" to 19.0, "Med" to 14.5, "Peq" to 7.5))
        )

        PizzaItemCategory.CALZONE -> listOf(
            PizzaItem("Calzone 1", "Jamon, queso y dos vegetales.", R.drawable.ic_calzone2_background, PizzaItemCategory.CALZONE, priceUsd = 6.0),
            PizzaItem("Calzone 2", "Jamon, tocineta y queso.", R.drawable.ic_calzone3_background, PizzaItemCategory.CALZONE, priceUsd = 6.0),
            PizzaItem("Calzone 3", "Pepperoni y queso.", R.drawable.ic_calzone4_background,
                PizzaItemCategory.CALZONE, priceUsd = 6.0)
            // ... agrega más si deseas
        )

        PizzaItemCategory.DEDOS_DE_QUESO -> listOf(
            PizzaItem("Dedos Clásicos", "Masa de pizza, dip de cheddar y queso mozzarella.", R.drawable.ic_dedos_queso_background,
                PizzaItemCategory.DEDOS_DE_QUESO, priceBySize = mapOf("EG" to 18.0, "Gde" to 13.0, "Med" to 10.0, "Peq" to 6.0))

            // ... agrega más si deseas
        )

        PizzaItemCategory.ROLLS -> listOf(
            PizzaItem("Rolls de Pizza", "Jamon, queso y tocineta.", R.drawable.ic_rolls_background,
                PizzaItemCategory.ROLLS, priceUsd = 5.0)

            // ... agrega más si deseas
        )

        PizzaItemCategory.EXTRAS -> listOf(
            PizzaItem("Cheddar", "El mas rico queso Cheddar", R.drawable.ic_ingredientes_background, PizzaItemCategory.EXTRAS, priceBySize = mapOf("EG" to 2.5, "Gde" to 2.0, "Med" to 1.5, "Peq" to 1.0)),
            PizzaItem("Champiñones", "Los mas frescos Champiñones", R.drawable.ic_ingredientes_background, PizzaItemCategory.EXTRAS, priceBySize = mapOf("EG" to 2.5, "Gde" to 2.0, "Med" to 1.5, "Peq" to 1.0)),
            PizzaItem("Aceitunas negras", "Aceitunas negras de excelente calidad", R.drawable.ic_ingredientes_background, PizzaItemCategory.EXTRAS, priceBySize = mapOf("EG" to 2.5, "Gde" to 2.0, "Med" to 1.5, "Peq" to 1.0)),
            PizzaItem("Aceitunas verdes", "Aceitunas verdes de excelente calidad", R.drawable.ic_ingredientes_background, PizzaItemCategory.EXTRAS,priceBySize = mapOf("EG" to 2.5, "Gde" to 2.0, "Med" to 1.5, "Peq" to 1.0)),
            PizzaItem("Anchoas", "Las mas exquisitas Anchoas", R.drawable.ic_ingredientes_background, PizzaItemCategory.EXTRAS, priceBySize = mapOf("EG" to 2.5, "Gde" to 2.0, "Med" to 1.5, "Peq" to 1.0))


            // ... agrega más si deseas
        )

        PizzaItemCategory.POSTRES -> listOf(
            PizzaItem("Pie de parchita", "El mas rico Pie de parchita", R.drawable.ic_postres3_background, PizzaItemCategory.POSTRES, priceUsd = 4.5),
            PizzaItem("Chesscake Fresa", "Fresa", R.drawable.ic_postres4_background, PizzaItemCategory.POSTRES, priceUsd = 6.5),
            PizzaItem("Chesscake Nutella", "Nutella", R.drawable.ic_postres5_background, PizzaItemCategory.POSTRES, priceUsd = 6.5)

            // ... agrega más si deseas
        )

        PizzaItemCategory.BEBIDAS -> listOf(
            PizzaItem("Cocacola 1.5 lt", "Botella plastica 1.5 lt", R.drawable.ic_refresco_coca_grande, PizzaItemCategory.BEBIDAS, priceUsd = 2.5),
            PizzaItem("Pepsi 1.5 lt", "Botella plastica 1.5 lt", R.drawable.ic_refresco_pepsi_grande, PizzaItemCategory.BEBIDAS, priceUsd = 2.5),
            PizzaItem("Freskolita 1.5 lt", "Botella plastica 1.5 lt", R.drawable.ic_refresco_freskolita_grande, PizzaItemCategory.BEBIDAS, priceUsd = 2.5),
            PizzaItem("Hit Uva 1.5 lt", "Botella plastica 1.5 lt", R.drawable.ic_refresco_uva_grande, PizzaItemCategory.BEBIDAS, priceUsd = 2.5),
            PizzaItem("Hit Naranja 1.5 lt", "Botella plastica 1.5 lt", R.drawable.ic_refresco_naranja_grande, PizzaItemCategory.BEBIDAS, priceUsd = 2.5),
            PizzaItem("Manzanita 1.5 lt", "Botella plastica 1.5 lt", R.drawable.ic_refresco_manzanita_grande, PizzaItemCategory.BEBIDAS, priceUsd = 2.5),
            PizzaItem("Chinotto 1.5 lt", "Botella plastica 1.5 lt", R.drawable.ic_refresco_chinotto_grande, PizzaItemCategory.BEBIDAS, priceUsd = 2.5),
            PizzaItem("Cocacola 1.0 lt", "Botella plastica 1.0 lt", R.drawable.ic_refresco_coca_mediana, PizzaItemCategory.BEBIDAS, priceUsd = 2.0),
            PizzaItem("Pepsi 1.0 lt", "Botella plastica 1.0 lt", R.drawable.ic_refresco_pepsi_mediana, PizzaItemCategory.BEBIDAS, priceUsd = 2.0),
            PizzaItem("FresKolita 1.0 lt", "Botella plastica 1.0 lt", R.drawable.ic_refresco_freskolita_mediana, PizzaItemCategory.BEBIDAS, priceUsd = 2.0),
            PizzaItem("Hit Uva 1.0 lt", "Botella plastica 1.0 lt", R.drawable.ic_refresco_uva_grande, PizzaItemCategory.BEBIDAS, priceUsd = 2.0),
            PizzaItem("Hit Naranja 1.0", "Botella plastica 1.0 lt", R.drawable.ic_refresco_naranja_grande, PizzaItemCategory.BEBIDAS, priceUsd = 2.0),
            PizzaItem("Manzanita 1.0", "Botella plastica 1.0 lt", R.drawable.ic_refresco_manzanita_grande, PizzaItemCategory.BEBIDAS, priceUsd = 2.0),
            PizzaItem("Chinotto 1.0", "Botella plastica 1.0 lt", R.drawable.ic_refresco_chinotto_mediano, PizzaItemCategory.BEBIDAS, priceUsd = 2.0),
            PizzaItem("Cocacola lata", "Refresco de lata", R.drawable.ic_refresco_coca_lata, PizzaItemCategory.BEBIDAS, priceUsd = 1.5),
            PizzaItem("Pepsi lata", "Refresco de lata", R.drawable.ic_refresco_pepsi_lata, PizzaItemCategory.BEBIDAS, priceUsd = 1.5),
            PizzaItem("Freskolita lata", "Refresco de lata", R.drawable.ic_refresco_frescolita_lata, PizzaItemCategory.BEBIDAS, priceUsd = 1.5),
            PizzaItem("Hit Naranja lata", "Refresco de lata", R.drawable.ic_refresco_naranja_lata, PizzaItemCategory.BEBIDAS, priceUsd = 1.5),
            PizzaItem("Chinotto lata", "Refresco de lata", R.drawable.ic_refresco_chinotto_lata, PizzaItemCategory.BEBIDAS, priceUsd = 1.5),
            PizzaItem("Te Lipton", "Bebida Refrescante", R.drawable.ic_lipton_background, PizzaItemCategory.BEBIDAS, priceUsd = 2.5),
                PizzaItem("Agua", "Agua mineral en Botella", R.drawable.ic_refresco_agua_mineral, PizzaItemCategory.BEBIDAS, priceUsd = 1.5)

        )

        else -> emptyList()
    }
}