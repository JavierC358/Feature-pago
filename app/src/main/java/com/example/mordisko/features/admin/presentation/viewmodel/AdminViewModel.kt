package com.example.mordisko.features.admin.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.max

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(AdminVerificacionesState())
    val state: StateFlow<AdminVerificacionesState> = _state.asStateFlow()

    private val _exchangeRate = MutableStateFlow(0.0)
    val exchangeRate: StateFlow<Double> = _exchangeRate

    // cursores por página: index 1 -> cursor null (página 1), index N -> lastDoc de la pág (N-1)
    private val pageCursors: MutableList<DocumentSnapshot?> = mutableListOf(null)
    private var isLoadingPage = false

    init {
        Log.d("TEST_INIT", "Entrando al init del AdminViewModel")
        viewModelScope.launch {
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                val uid = auth.currentUser?.uid ?: return@launch

                Log.d("🔥FCM_TOKEN", "Token del admin: $token")

                firestore.collection("admin_tokens").document(uid).set(
                    mapOf("token" to token)
                ).await()

                Log.d("🔥FCM_TOKEN", "Token guardado exitosamente en admin_tokens.")
            } catch (e: Exception) {
                Log.e("🔥FCM_TOKEN", "Error obteniendo o guardando el token FCM", e)
            }
        }
    }

    fun setFilter(filter: OrdersFilter) {
        if (state.value.filter == filter) return
        _state.update { it.copy(filter = filter) }
        onChangePageSize(state.value.pageSize) // resetea paginación con el filtro actual
    }

    /** --- Paginación pública --- */

    fun loadVerificaciones() {
        // Carga inicial con el pageSize actual (página 1)
        onChangePageSize(state.value.pageSize)
    }

    fun onChangePageSize(newSize: Int) {
        if (newSize <= 0) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, pageSize = newSize, currentPage = 1) }
            pageCursors.clear(); pageCursors.add(null)

            val totalCount = getTotalOrdersCount()
            val totalPages = calcTotalPages(totalCount, newSize)

            val (list, last) = getOrdersPage(newSize.toLong(), null)
            if (pageCursors.size <= 1) pageCursors.add(last) else pageCursors[1] = last

            _state.update {
                it.copy(
                    verificaciones = list,
                    isLoading = false,
                    totalCount = totalCount,
                    totalPages = totalPages,
                    currentPage = 1
                )
            }
        }
    }

    fun nextPage() {
        val s = state.value
        val next = s.currentPage + 1
        if (next > s.totalPages || s.isLoading) return
        loadPage(next)
    }

    fun prevPage() {
        val s = state.value
        val prev = s.currentPage - 1
        if (prev < 1 || s.isLoading) return
        loadPage(prev)
    }

    /** --- Acciones --- */

    fun marcarComoVerificada(orderNumber: String) {
        viewModelScope.launch {
            try {
                val orderRef = firestore.collection("orders").document(orderNumber)
                val infoRef = orderRef.collection("payment_verification").document("info")

                // 1) Actualiza SIEMPRE el campo principal (esto es lo que usan tus reportes)
                orderRef.update("paymentStatus", "verificado").await()

                // 2) Actualiza/crea la subcolección (para que la UI admin también quede pareja)
                val infoSnap = infoRef.get().await()
                if (infoSnap.exists()) {
                    infoRef.update("status", "verificado").await()
                } else {
                    // si nunca hubo pago móvil, creamos un registro mínimo
                    infoRef.set(
                        mapOf(
                            "status" to "verificado"
                            // puedes incluir más campos si quieres trazabilidad
                        )
                    ).await()
                }

                // 3) Recargar la página actual (sin perder paginación)
                val current = state.value.currentPage
                loadPage(current) // si no tienes paginación aquí, usa loadVerificaciones()
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Error marcando como verificada", e)
            }
        }
    }

    /** --- Tasa de cambio (sin cambios) --- */

    fun loadExchangeRate() {
        viewModelScope.launch {
            val doc = firestore.collection("config").document("settings").get().await()
            val tasa = doc.getDouble("exchangeRateUsdToVes") ?: 0.0
            _exchangeRate.value = tasa
        }
    }

    fun guardarExchangeRate(nuevaTasa: Double) {
        viewModelScope.launch {
            firestore.collection("config").document("settings")
                .update("exchangeRateUsdToVes", nuevaTasa)
        }
    }

    fun guardarTokenFcm() {
        viewModelScope.launch {
            try {
                val uid = auth.currentUser?.uid ?: return@launch
                val token = FirebaseMessaging.getInstance().token.await()

                val userDocRef = firestore.collection("users").document(uid)
                val snapshot = userDocRef.get().await()
                val existingTokens = snapshot.get("fcmTokens") as? List<String> ?: emptyList()

                if (!existingTokens.contains(token)) {
                    val updatedTokens = existingTokens.toMutableList().apply { add(token) }
                    userDocRef.update("fcmTokens", updatedTokens)
                    Log.d("FCM", "Token FCM agregado correctamente.")
                } else {
                    Log.d("FCM", "Token FCM ya existe, no se actualiza.")
                }
            } catch (e: Exception) {
                Log.e("FCM", "Error guardando token FCM: ${e.message}")
            }
        }
    }

    /** --- Internos de paginación --- */

    private fun calcTotalPages(total: Long, size: Int): Int =
        if (size <= 0) 1 else max(1, ceil(total.toDouble() / size).toInt())

    private fun loadPage(page: Int) {
        if (isLoadingPage) return
        isLoadingPage = true
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val s = state.value
                val size = s.pageSize.toLong()

                // Cursor para página N = lastDoc de la página N-1
                val cursor = if (page - 1 < pageCursors.size) {
                    pageCursors[page - 1]
                } else {
                    // Si intentan saltar, avanzamos precalculando cursores
                    var c: DocumentSnapshot? = pageCursors.last()
                    var p = pageCursors.size
                    while (p < page) {
                        val (_, last) = getOrdersPage(size, c)
                        pageCursors.add(last)
                        c = last
                        p++
                        if (last == null) break
                    }
                    pageCursors.getOrNull(page - 1)
                }

                val (list, last) = getOrdersPage(size, cursor)
                if (pageCursors.size <= page) pageCursors.add(last) else pageCursors[page] = last

                _state.update {
                    it.copy(
                        verificaciones = list,
                        isLoading = false,
                        currentPage = page
                    )
                }
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Error loadPage($page): ${e.message}", e)
                _state.update { it.copy(isLoading = false) }
            } finally {
                isLoadingPage = false
            }
        }
    }

    private suspend fun getTotalOrdersCount(): Long {
        return try {
            val base = firestore.collection("orders")
            val query = when (state.value.filter) {
                OrdersFilter.POR_VERIFICAR -> base.whereEqualTo("paymentStatus", "por_verificar")
                OrdersFilter.TODAS         -> base
            }
            query.count().get(AggregateSource.SERVER).await().count
        } catch (e: Exception) {
            Log.e("AdminViewModel", "count() fallo: ${e.message}", e)
            0L
        }
    }
    private suspend fun getOrdersPage(
        pageSize: Long,
        lastDoc: DocumentSnapshot?
    ): Pair<List<VerificacionPago>, DocumentSnapshot?> {

        var q: Query = firestore.collection("orders")

        if (state.value.filter == OrdersFilter.POR_VERIFICAR) {
            q = q.whereEqualTo("paymentStatus", "por_verificar")
        }

        q = q.orderBy("timestamp", Query.Direction.DESCENDING).limit(pageSize)

        if (lastDoc != null) q = q.startAfter(lastDoc)

        val snap = q.get().await()
        val docs = snap.documents

        val result = mutableListOf<VerificacionPago>()

        for (order in docs) {
            val orderId = order.id

            // ✅ Total SIEMPRE desde orders
            val totalBs = order.getDouble("totalBs")

            val pagoDoc = firestore.collection("orders")
                .document(orderId)
                .collection("payment_verification")
                .document("info")
                .get()
                .await()

            // ✅ Caso A: existe info de pago móvil
            val amountPaid = if (pagoDoc.exists()) pagoDoc.getDouble("amountPaid") else null
            val referenceLast4 = if (pagoDoc.exists()) pagoDoc.getString("referenceLast4") ?: "--" else "--"
            val phoneNumber = if (pagoDoc.exists()) pagoDoc.getString("phoneNumber") ?: "--" else "--"

            val status = normalizeStatus(
                (if (pagoDoc.exists()) pagoDoc.getString("status") else null)
                    ?: order.getString("paymentStatus")
            )

            result.add(
                VerificacionPago(
                    orderNumber = orderId,
                    totalBs = totalBs,
                    amountPaid = amountPaid,
                    referenceLast4 = referenceLast4,
                    phoneNumber = phoneNumber,
                    status = status
                )
            )
        }

        val newLast = docs.lastOrNull()
        return result to newLast
    }
}

private fun normalizeStatus(s: String?): String {
    val v = s?.trim()?.lowercase() ?: ""
    return when {
        v == "por_verificar" -> "por_verificar"   // ⬅️ manejar primero este estado
        "verific" in v       -> "verificado"      // "verificado", "verificada", etc.
        "pend" in v          -> "pendiente"
        else                 -> v                 // deja cualquier otro valor tal cual
    }
}

enum class OrdersFilter { POR_VERIFICAR, TODAS }

/** --- State y modelo: se agregan campos para paginación visible --- */
data class AdminVerificacionesState(
    val isLoading: Boolean = false,
    val verificaciones: List<VerificacionPago> = emptyList(),
    val pageSize: Int = 10,          // Reg… (5/10/20)
    val currentPage: Int = 1,        // 1-based
    val totalPages: Int = 1,         // calculado con count()
    val totalCount: Long = 0L,       // opcional
    val filter: OrdersFilter = OrdersFilter.POR_VERIFICAR  // 👈 por defecto
)

data class VerificacionPago(
    val orderNumber: String,
    val totalBs: Double?,     // ✅ total de la orden
    val amountPaid: Double?,  // ✅ lo que pagó por pago móvil (si existe)
    val referenceLast4: String,
    val phoneNumber: String,
    val status: String
)