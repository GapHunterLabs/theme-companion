package com.example.demo

import java.time.Instant
import kotlin.math.max

/**
 * Tracks warehouse stock levels and raises a [LowStockAlert] whenever an
 * item drops below its configured reorder threshold.
 */
data class InventoryItem(
    val sku: String,
    val name: String,
    var quantity: Int,
    val reorderThreshold: Int = 10,
    val lastRestocked: Instant = Instant.now(),
)

class LowStockAlert(val item: InventoryItem, val message: String) : RuntimeException(message)

class InventoryService(private val items: MutableMap<String, InventoryItem> = mutableMapOf()) {

    private val listeners = mutableListOf<(InventoryItem) -> Unit>()

    fun register(item: InventoryItem) {
        items[item.sku] = item
    }

    fun onLowStock(listener: (InventoryItem) -> Unit) {
        listeners += listener
    }

    fun consume(sku: String, amount: Int): Boolean {
        val item = items[sku] ?: throw NoSuchElementException("Unknown SKU: $sku")
        if (item.quantity < amount) {
            return false
        }
        item.quantity = max(0, item.quantity - amount)

        if (item.quantity <= item.reorderThreshold) {
            val alert = "Low stock for ${item.name} (${item.quantity} left, threshold ${item.reorderThreshold})"
            listeners.forEach { it(item) }
            println(alert)
        }
        return true
    }

    fun restock(sku: String, amount: Int) {
        items[sku]?.let { it.quantity += amount } ?: error("Cannot restock unknown SKU $sku")
    }

    fun snapshot(): List<InventoryItem> = items.values.sortedBy { it.quantity }
}

fun main() {
    val service = InventoryService()
    service.register(InventoryItem(sku = "SKU-001", name = "USB-C Cable", quantity = 42))
    service.register(InventoryItem(sku = "SKU-002", name = "Mechanical Keyboard", quantity = 8, reorderThreshold = 5))

    service.onLowStock { item -> println("ALERT: reorder ${item.sku} soon") }

    service.consume("SKU-001", amount = 15)
    service.consume("SKU-002", amount = 4)

    for (item in service.snapshot()) {
        println("${item.sku}: ${item.name} -> ${item.quantity} units")
    }
}
