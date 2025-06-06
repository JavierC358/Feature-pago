package com.example.mordisko.features.menu.domain.model

import com.example.mordisko.R
import com.example.mordisko.features.menu.domain.util.mapStringToPizzaItemCategory

fun getPizzaItemsForCategory(category: String): List<PizzaItem> {
    val enumCategory = mapStringToPizzaItemCategory(category)

    return when (enumCategory) {
        PizzaItemCategory.PIZZAS -> listOf(
            PizzaItem("Tipo 1", "Tomate, mozzarella y albahaca", R.drawable.ic_pizzas_background, PizzaItemCategory.PIZZAS),
            PizzaItem("Tipo 2", "Jamón y piña", R.drawable.ic_pizzas_background, PizzaItemCategory.PIZZAS),
            PizzaItem("Tipo 3", "Mozzarella, parmesano, azul y cheddar", R.drawable.ic_pizzas_background, PizzaItemCategory.PIZZAS),
            PizzaItem("Tipo 4", "Mozzarella y pepperoni", R.drawable.ic_pizzas_background,PizzaItemCategory.PIZZAS ),
            PizzaItem("La Libre", "Vegetales frescos y mozzarella", R.drawable.ic_pizzas_background,PizzaItemCategory.PIZZAS ),
            PizzaItem("Macagr Burger", "Jalapeños, carne y maíz", R.drawable.ic_pizzas_background, PizzaItemCategory.PIZZAS),
            PizzaItem("Super Macagr", "Pollo, BBQ y cebolla", R.drawable.ic_pizzas_background, PizzaItemCategory.PIZZAS),
            PizzaItem("Waikiki", "Jamón, champiñones, alcachofas", R.drawable.ic_pizzas_background,PizzaItemCategory.PIZZAS ),
            PizzaItem("Todo Terreno", "Anchoas y aceitunas", R.drawable.ic_pizzas_background, PizzaItemCategory.PIZZAS),
            PizzaItem("Full Cheese", "Bacon, huevo y parmesano", R.drawable.ic_pizzas_background,PizzaItemCategory.PIZZAS ),
            PizzaItem("Chicken Bacon", "Aceite de trufa y queso crema", R.drawable.ic_pizzas_background, PizzaItemCategory.PIZZAS),
            PizzaItem("Caprichosa", "Carne, queso y salsas", R.drawable.ic_pizzas_background, PizzaItemCategory.PIZZAS),
            PizzaItem("Vegetariana", "Mozzarella de búfala y tomate cherry", R.drawable.ic_pizzas_background, PizzaItemCategory.PIZZAS),
            PizzaItem("Bahia", "Jamón serrano y rúgula", R.drawable.ic_pizzas_background, PizzaItemCategory.PIZZAS),
            PizzaItem("Favorite de Jend's", "Clásica y sabrosa", R.drawable.ic_pizzas_background, PizzaItemCategory.PIZZAS)
        )

        PizzaItemCategory.CALZONE -> listOf(
            PizzaItem("Calzone 1", "Jamon, queso y dos vegetales.", R.drawable.ic_calzone_background, PizzaItemCategory.CALZONE),
            PizzaItem("Calzone 2", "Jamon, tocineta y queso.", R.drawable.ic_calzone_background, PizzaItemCategory.CALZONE),
            PizzaItem("Calzone 3", "Pepperoni y queso.", R.drawable.ic_calzone_background,PizzaItemCategory.CALZONE)
            // ... agrega más si deseas
        )

        PizzaItemCategory.DEDOS_DE_QUESO -> listOf(
            PizzaItem("Dedos Clásicos", "Queso mozzarella empanizado", R.drawable.ic_dedos_queso_background,PizzaItemCategory.DEDOS_DE_QUESO)

            // ... agrega más si deseas
        )

        PizzaItemCategory.ROLLS -> listOf(
            PizzaItem("Rolls de Pizza", "Queso mozzarella empanizado", R.drawable.ic_rolls_background,PizzaItemCategory.ROLLS )

            // ... agrega más si deseas
        )

        PizzaItemCategory.EXTRAS -> listOf(
            PizzaItem("Extras", "Queso mozzarella empanizado", R.drawable.ic_ingredientes_background, PizzaItemCategory.EXTRAS),
            PizzaItem("Extras", "Queso mozzarella empanizado", R.drawable.ic_ingredientes_background, PizzaItemCategory.EXTRAS),
            PizzaItem("Extras", "Queso mozzarella empanizado", R.drawable.ic_ingredientes_background, PizzaItemCategory.EXTRAS),
            PizzaItem("Extras", "Queso mozzarella empanizado", R.drawable.ic_ingredientes_background, PizzaItemCategory.EXTRAS),
            PizzaItem("Extras", "Queso mozzarella empanizado", R.drawable.ic_ingredientes_background, PizzaItemCategory.EXTRAS),
            PizzaItem("Extras", "Queso mozzarella empanizado", R.drawable.ic_ingredientes_background, PizzaItemCategory.EXTRAS),
            PizzaItem("Extras", "Queso mozzarella empanizado", R.drawable.ic_ingredientes_background, PizzaItemCategory.EXTRAS)

            // ... agrega más si deseas
        )

        PizzaItemCategory.POSTRES -> listOf(
            PizzaItem("Postres", "Queso mozzarella empanizado", R.drawable.ic_postre_background, PizzaItemCategory.POSTRES),
            PizzaItem("Postres", "Queso mozzarella empanizado", R.drawable.ic_postre_background, PizzaItemCategory.POSTRES)


            // ... agrega más si deseas
        )

        PizzaItemCategory.BEBIDAS -> listOf(
            PizzaItem("Cocacola 1.5 lt", "Tomate, mozzarella y albahaca", R.drawable.ic_refrescos_background, PizzaItemCategory.BEBIDAS),
            PizzaItem("Cocacola 2.0 lt", "Jamón y piña", R.drawable.ic_refrescos_background, PizzaItemCategory.BEBIDAS),
            PizzaItem("Pepsi 1.5 lt", "Mozzarella, parmesano, azul y cheddar", R.drawable.ic_refrescos_background, PizzaItemCategory.BEBIDAS),
            PizzaItem("Chinotto 1.5 lt", "Mozzarella y pepperoni", R.drawable.ic_refrescos_background, PizzaItemCategory.BEBIDAS),
            PizzaItem("Chinotto 2.0 lt", "Vegetales frescos y mozzarella", R.drawable.ic_refrescos_background, PizzaItemCategory.BEBIDAS),
            PizzaItem("Frescolita 1.5 lt", "Jalapeños, carne y maíz", R.drawable.ic_refrescos_background, PizzaItemCategory.BEBIDAS),
            PizzaItem("Frescolita 2.0 lt", "Pollo, BBQ y cebolla", R.drawable.ic_refrescos_background, PizzaItemCategory.BEBIDAS),
            PizzaItem("Hit Naranja 1.5 lt", "Jamón, champiñones, alcachofas", R.drawable.ic_refrescos_background, PizzaItemCategory.BEBIDAS),
            PizzaItem("Hit Naranja 2.0 lt", "Anchoas y aceitunas", R.drawable.ic_refrescos_background, PizzaItemCategory.BEBIDAS),
            PizzaItem("Hit Uva 1.5 lt", "Bacon, huevo y parmesano", R.drawable.ic_refrescos_background, PizzaItemCategory.BEBIDAS),
            PizzaItem("Hit Uva 2.0 lt", "Aceite de trufa y queso crema", R.drawable.ic_refrescos_background, PizzaItemCategory.BEBIDAS),
            PizzaItem("Cocacola lata", "Carne, queso y salsas", R.drawable.ic_lata_background, PizzaItemCategory.BEBIDAS),
            PizzaItem("Pepsi lata", "Mozzarella de búfala y tomate cherry", R.drawable.ic_lata_background, PizzaItemCategory.BEBIDAS),
            PizzaItem("Chinotto lata", "Jamón serrano y rúgula", R.drawable.ic_lata_background, PizzaItemCategory.BEBIDAS),
            PizzaItem("Te Lipton", "Clásica y sabrosa", R.drawable.ic_lipton_background, PizzaItemCategory.BEBIDAS),
                PizzaItem("Agua", "Clásica y sabrosa", R.drawable.ic_agua_background, PizzaItemCategory.BEBIDAS)

        )

        else -> emptyList()
    }
}