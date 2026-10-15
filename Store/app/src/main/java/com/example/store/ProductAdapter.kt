package com.example.store

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.store.databinding.ItemProductBinding

class ProductAdapter(
    private var products: List<Product>,
    private val onAddToCart: (Product) -> Unit,
    private val onIncrease: (Product) -> Unit,
    private val onDecrease: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.tvTitle.text = product.title
            binding.tvPrice.text = "$${product.price}"
            binding.tvCategory.text = product.category
            binding.tvRating.text = "★ ${product.rating?.rate ?: 0.0}"

            Glide.with(binding.ivProduct.context)
                .load(product.image)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(binding.ivProduct)

            val qty = CartManager.getQuantity(product.id)
            if (qty > 0) {
                binding.btnAddToCart.visibility = View.GONE
                binding.layoutQuantity.visibility = View.VISIBLE
                binding.tvQuantity.text = qty.toString()
            } else {
                binding.btnAddToCart.visibility = View.VISIBLE
                binding.layoutQuantity.visibility = View.GONE
            }

            binding.btnAddToCart.setOnClickListener {
                onAddToCart(product)
                notifyItemChanged(adapterPosition)
            }

            binding.btnPlus.setOnClickListener {
                onIncrease(product)
                notifyItemChanged(adapterPosition)
            }

            binding.btnMinus.setOnClickListener {
                onDecrease(product)
                notifyItemChanged(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size
}
