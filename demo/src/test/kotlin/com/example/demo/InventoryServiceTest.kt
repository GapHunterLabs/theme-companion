package com.example.demo

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InventoryServiceTest {

    @Test
    fun `consume reduces quantity`() {
        val service = InventoryService()
        service.register(InventoryItem(sku = "SKU-100", name = "Widget", quantity = 20))

        val ok = service.consume("SKU-100", amount = 5)

        assertTrue(ok)
        assertEquals(15, service.snapshot().first().quantity)
    }

    @Test
    fun `consume fails when insufficient stock`() {
        val service = InventoryService()
        service.register(InventoryItem(sku = "SKU-101", name = "Gadget", quantity = 3))

        val ok = service.consume("SKU-101", amount = 10)

        assertFalse(ok)
    }
}
