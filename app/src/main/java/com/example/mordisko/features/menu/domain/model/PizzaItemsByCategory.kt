package com.example.mordisko.features.menu.domain.model

import com.example.mordisko.R
import com.example.mordisko.features.menu.domain.util.mapStringToPizzaItemCategory

fun getPizzaItemsForCategory(category: String): List<PizzaItem> {
    val enumCategory = mapStringToPizzaItemCategory(category)

    return when (enumCategory) {
        PizzaItemCategory.PIZZAS -> listOf(
            PizzaItem("Tipo 1", "Tomate, mozzarella y albahaca", R.drawable.ic_pizzas1_menu, PizzaItemCategory.PIZZAS,priceBySize = mapOf("EG" to 12.0, "Gde" to 10.0, "Med" to 8.0, "Peq" to 6.0)),
            PizzaItem("Tipo 2", "Jamón y piña", R.drawable.ic_pizzas2_menu, PizzaItemCategory.PIZZAS,priceBySize = mapOf("EG" to 13.0, "Gde" to 10.5, "Med" to 8.5, "Peq" to 6.5)),
            PizzaItem("Tipo 3", "Mozzarella, parmesano, azul y cheddar", R.drawable.ic_pizzas3_menu, PizzaItemCategory.PIZZAS,priceBySize = mapOf("EG" to 14.0, "Gde" to 11.0, "Med" to 9.0, "Peq" to 7.0)),
            PizzaItem("Tipo 4", "Mozzarella y pepperoni", R.drawable.ic_pizzas4_menu,PizzaItemCategory.PIZZAS,priceBySize = mapOf("EG" to 13.5, "Gde" to 11.0, "Med" to 9.4, "Peq" to 7.0)),
            PizzaItem("La Libre", "Vegetales frescos y mozzarella", R.drawable.ic_pizzas5_menu,PizzaItemCategory.PIZZAS,priceBySize = mapOf("EG" to 14.0, "Gde" to 12.0, "Med" to 10.0, "Peq" to 8.0)),
            PizzaItem("Macagr Burger", "Jalapeños, carne y maíz", R.drawable.ic_pizzas6_menu, PizzaItemCategory.PIZZAS, priceBySize = mapOf("EG" to 15.0, "Gde" to 13.0, "Med" to 11.0, "Peq" to 9.0)),
            PizzaItem("Super Macagr", "Pollo, BBQ y cebolla", R.drawable.ic_pizzas7_menu, PizzaItemCategory.PIZZAS, priceBySize = mapOf("EG" to 13.0, "Gde" to 11.0, "Med" to 9.0, "Peq" to 7.0)),
            PizzaItem("Waikiki", "Jamón, champiñones, alcachofas", R.drawable.ic_pizzas8_menu,PizzaItemCategory.PIZZAS, priceBySize = mapOf("EG" to 14.0, "Gde" to 12.0, "Med" to 9.5, "Peq" to 7.5)),
            PizzaItem("Todo Terreno", "Anchoas y aceitunas", R.drawable.ic_pizzas9_menu, PizzaItemCategory.PIZZAS, priceBySize = mapOf("EG" to 13.0, "Gde" to 11.0, "Med" to 9.0, "Peq" to 7.0)),
            PizzaItem("Full Cheese", "Bacon, huevo y parmesano", R.drawable.ic_pizzas10_menu,PizzaItemCategory.PIZZAS, priceBySize = mapOf("EG" to 14.0, "Gde" to 12.0, "Med" to 10.0, "Peq" to 8.0)),
            PizzaItem("Chicken Bacon", "Aceite de trufa y queso crema", R.drawable.ic_pizzas11_menu, PizzaItemCategory.PIZZAS,priceBySize = mapOf("EG" to 13.2, "Gde" to 11.5, "Med" to 9.2, "Peq" to 7.2)),
            PizzaItem("Caprichosa", "Carne, queso y salsas", R.drawable.ic_pizzas12_menu, PizzaItemCategory.PIZZAS, priceBySize = mapOf("EG" to 15.0, "Gde" to 13.0, "Med" to 12.0, "Peq" to 9.0)),
            PizzaItem("Vegetariana", "Mozzarella de búfala y tomate cherry", R.drawable.ic_pizzas13_menu, PizzaItemCategory.PIZZAS, priceBySize = mapOf("EG" to 13.0, "Gde" to 11.0, "Med" to 9.0, "Peq" to 7.0)),
            PizzaItem("Bahia", "Jamón serrano y rúgula", R.drawable.ic_pizzas14_menu, PizzaItemCategory.PIZZAS,priceBySize = mapOf("EG" to 14.0, "Gde" to 12.0, "Med" to 9.5, "Peq" to 7.5)),
            PizzaItem("Favorite de Jend's", "Clásica y sabrosa", R.drawable.ic_pizzas15_menu, PizzaItemCategory.PIZZAS, priceBySize = mapOf("EG" to 13.0, "Gde" to 11.0, "Med" to 9.0, "Peq" to 7.0))
        )

        PizzaItemCategory.CALZONE -> listOf(
            PizzaItem("Calzone 1", "Jamon, queso y dos vegetales.", R.drawable.ic_calzone_background, PizzaItemCategory.CALZONE, priceUsd = 6.0),
            PizzaItem("Calzone 2", "Jamon, tocineta y queso.", R.drawable.ic_calzone_background, PizzaItemCategory.CALZONE, priceUsd = 6.0),
            PizzaItem("Calzone 3", "Pepperoni y queso.", R.drawable.ic_calzone_background,PizzaItemCategory.CALZONE, priceUsd = 6.0)
            // ... agrega más si deseas
        )

        PizzaItemCategory.DEDOS_DE_QUESO -> listOf(
            PizzaItem("Dedos Clásicos", "Queso mozzarella empanizado", R.drawable.ic_dedos_queso_background,PizzaItemCategory.DEDOS_DE_QUESO, priceBySize = mapOf("EG" to 15.0, "Gde" to 14.0, "Med" to 13.0, "Peq" to 12.0))

            // ... agrega más si deseas
        )

        PizzaItemCategory.ROLLS -> listOf(
            PizzaItem("Rolls de Pizza", "Queso mozzarella empanizado", R.drawable.ic_rolls_background,PizzaItemCategory.ROLLS, priceUsd = 2.0)

            // ... agrega más si deseas
        )

        PizzaItemCategory.EXTRAS -> listOf(
            PizzaItem("Cheddar", "Queso mozzarella empanizado", R.drawable.ic_ingredientes_background, PizzaItemCategory.EXTRAS, priceBySize = mapOf("EG" to 15.0, "Gde" to 14.0, "Med" to 13.0, "Peq" to 12.0)),
            PizzaItem("Champiñones", "Queso mozzarella empanizado", R.drawable.ic_ingredientes_background, PizzaItemCategory.EXTRAS, priceBySize = mapOf("EG" to 15.0, "Gde" to 14.0, "Med" to 13.0, "Peq" to 12.0)),
            PizzaItem("Aceitunas negras", "Queso mozzarella empanizado", R.drawable.ic_ingredientes_background, PizzaItemCategory.EXTRAS, priceBySize = mapOf("EG" to 15.0, "Gde" to 14.0, "Med" to 13.0, "Peq" to 12.0)),
            PizzaItem("Aceitunas verdes", "Queso mozzarella empanizado", R.drawable.ic_ingredientes_background, PizzaItemCategory.EXTRAS,priceBySize = mapOf("EG" to 15.0, "Gde" to 14.0, "Med" to 13.0, "Peq" to 12.0)),
            PizzaItem("Anchoas", "Queso mozzarella empanizado", R.drawable.ic_ingredientes_background, PizzaItemCategory.EXTRAS, priceBySize = mapOf("EG" to 15.0, "Gde" to 14.0, "Med" to 13.0, "Peq" to 12.0))


            // ... agrega más si deseas
        )

        PizzaItemCategory.POSTRES -> listOf(
            PizzaItem("Postres", "Queso mozzarella empanizado", R.drawable.ic_postres_background, PizzaItemCategory.POSTRES, priceUsd = 2.0),
            PizzaItem("Postres", "Queso mozzarella empanizado", R.drawable.ic_postres_background, PizzaItemCategory.POSTRES, priceUsd = 2.0)


            // ... agrega más si deseas
        )

        PizzaItemCategory.BEBIDAS -> listOf(
            PizzaItem("Cocacola 1.5 lt", "Tomate, mozzarella y albahaca", R.drawable.ic_refrescos_background, PizzaItemCategory.BEBIDAS, priceUsd = 2.0),
            PizzaItem("Cocacola 2.0 lt", "Jamón y piña", R.drawable.ic_refrescos_background, PizzaItemCategory.BEBIDAS, priceUsd = 2.0),
            PizzaItem("Pepsi 1.5 lt", "Mozzarella, parmesano, azul y cheddar", R.drawable.ic_refrescos_background, PizzaItemCategory.BEBIDAS, priceUsd = 2.0),
            PizzaItem("Chinotto 1.5 lt", "Mozzarella y pepperoni", R.drawable.ic_refrescos_background, PizzaItemCategory.BEBIDAS, priceUsd = 2.0),
            PizzaItem("Chinotto 2.0 lt", "Vegetales frescos y mozzarella", R.drawable.ic_refrescos_background, PizzaItemCategory.BEBIDAS, priceUsd = 2.0),
            PizzaItem("Frescolita 1.5 lt", "Jalapeños, carne y maíz", R.drawable.ic_refrescos_background, PizzaItemCategory.BEBIDAS, priceUsd = 2.0),
            PizzaItem("Frescolita 2.0 lt", "Pollo, BBQ y cebolla", R.drawable.ic_refrescos_background, PizzaItemCategory.BEBIDAS, priceUsd = 2.0),
            PizzaItem("Hit Naranja 1.5 lt", "Jamón, champiñones, alcachofas", R.drawable.ic_refrescos_background, PizzaItemCategory.BEBIDAS, priceUsd = 2.0),
            PizzaItem("Hit Naranja 2.0 lt", "Anchoas y aceitunas", R.drawable.ic_refrescos_background, PizzaItemCategory.BEBIDAS, priceUsd = 2.0),
            PizzaItem("Hit Uva 1.5 lt", "Bacon, huevo y parmesano", R.drawable.ic_refrescos_background, PizzaItemCategory.BEBIDAS, priceUsd = 2.0),
            PizzaItem("Hit Uva 2.0 lt", "Aceite de trufa y queso crema", R.drawable.ic_refrescos_background, PizzaItemCategory.BEBIDAS, priceUsd = 2.0),
            PizzaItem("Cocacola lata", "Carne, queso y salsas", R.drawable.ic_lata_background, PizzaItemCategory.BEBIDAS, priceUsd = 2.0),
            PizzaItem("Pepsi lata", "Mozzarella de búfala y tomate cherry", R.drawable.ic_lata_background, PizzaItemCategory.BEBIDAS, priceUsd = 2.0),
            PizzaItem("Chinotto lata", "Jamón serrano y rúgula", R.drawable.ic_lata_background, PizzaItemCategory.BEBIDAS, priceUsd = 2.0),
            PizzaItem("Te Lipton", "Clásica y sabrosa", R.drawable.ic_lipton_background, PizzaItemCategory.BEBIDAS, priceUsd = 2.0),
                PizzaItem("Agua", "Clásica y sabrosa", R.drawable.ic_agua_background, PizzaItemCategory.BEBIDAS, priceUsd = 2.0)

        )

        else -> emptyList()
    }
}