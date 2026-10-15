package com.example.store

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.store.databinding.ItemCartBinding

class CartAdapter(
    private val cartItems: List<CartItem>,
    private val onIncrease: (Product) -> Unit,
    private val onDecrease: (Product) -> Unit,
    private val onCartChanged: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: CartItem) {
            val product = cartItem.product
            binding.tvCartTitle.text = product.title
            binding.tvCartPrice.text = "$${product.price}"
            binding.tvCartQuantity.text = cartItem.quantity.toString()

            Glide.with(binding.ivCartItem.context)
                .load(product.image)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(binding.ivCartItem)

            binding.btnCartPlus.setOnClickListener {
                onIncrease(product)
                notifyItemChanged(adapterPosition)
                onCartChanged()
            }

            binding.btnCartMinus.setOnClickListener {
                onDecrease(product)
                if (cartItem.quantity <= 0) {
                    notifyDataSetChanged()
                } else {
                    notifyItemChanged(adapterPosition)
                }
                onCartChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount(): Int = cartItems.size
}
