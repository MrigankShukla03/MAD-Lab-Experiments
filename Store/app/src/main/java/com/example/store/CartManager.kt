package com.example.store

data class CartItem(
    val product: Product,
    var quantity: Int
)

object CartManager {
    private val cartItems = mutableListOf<CartItem>()

    fun getCartItems(): List<CartItem> = cartItems

    fun addToCart(product: Product) {
        val existing = cartItems.find { it.product.id == product.id }
        if (existing != null) {
            existing.quantity++
        } else {
            cartItems.add(CartItem(product, 1))
        }
    }

    fun removeFromCart(product: Product) {
        val existing = cartItems.find { it.product.id == product.id }
        if (existing != null) {
            if (existing.quantity > 1) {
                existing.quantity--
            } else {
                cartItems.remove(existing)
            }
        }
    }

    fun getQuantity(productId: Int): Int {
        return cartItems.find { it.product.id == productId }?.quantity ?: 0
    }

    fun getTotalItemsCount(): Int {
        return cartItems.sumOf { it.quantity }
    }

    fun getTotalPrice(): Double {
        return cartItems.sumOf { it.product.price * it.quantity }
    }

    fun clearCart() {
        cartItems.clear()
    }
}
