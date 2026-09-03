package com.example.shareplate.data.local

import android.content.Context

/**
 * Remembers the NGO user's favourites and completed orders on this device,
 * so they survive logout / app restarts.
 */
class NgoLocalStore(context: Context) {

    private val prefs = context.getSharedPreferences("ngo_data", Context.MODE_PRIVATE)

    fun getFavourites(): Set<String> =
        prefs.getStringSet(KEY_FAVOURITES, emptySet()) ?: emptySet()

    fun setFavourite(shopName: String, liked: Boolean) {
        val set = getFavourites().toMutableSet()
        if (liked) set.add(shopName) else set.remove(shopName)
        prefs.edit().putStringSet(KEY_FAVOURITES, set).apply()
    }

    fun getOrders(): List<String> {
        val raw = prefs.getString(KEY_ORDERS, "") ?: ""
        return if (raw.isBlank()) emptyList() else raw.split(ORDER_SEP).filter { it.isNotBlank() }
    }

    fun addOrder(order: String) {
        val list = getOrders().toMutableList()
        list.add(0, order)
        prefs.edit().putString(KEY_ORDERS, list.take(50).joinToString(ORDER_SEP)).apply()
    }

    fun clearOrders() {
        prefs.edit().remove(KEY_ORDERS).apply()
    }

    companion object {
        private const val KEY_FAVOURITES = "favourites"
        private const val KEY_ORDERS = "orders"
        private const val ORDER_SEP = ";;"
    }
}