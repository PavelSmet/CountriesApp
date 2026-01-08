package com.example.countriesapp.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.countriesapp.R
import com.example.countriesapp.data.model.Country
import com.example.countriesapp.databinding.ItemCountryBinding

class CountryAdapter(
    private val onItemClick: (Country) -> Unit,
    private val onFavoriteClick: (Country) -> Unit
) : ListAdapter<Country, CountryAdapter.CountryViewHolder>(CountryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        Log.d("CountryAdapter", "🆕 Создаю ViewHolder")
        val binding = ItemCountryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CountryViewHolder(binding, onItemClick, onFavoriteClick)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val country = getItem(position)
        Log.v("CountryAdapter", "📱 Биндим позицию $position: ${country.name}")
        holder.bind(country)
    }

    // ⭐ ДОБАВЬ ЭТОТ МЕТОД для частичных обновлений
    override fun onBindViewHolder(holder: CountryViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isNotEmpty() && payloads[0] == true) {
            val country = getItem(position)
            holder.updateFavoriteIcon(country.isFavorite)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun getItemCount(): Int {
        val count = super.getItemCount()
        Log.d("CountryAdapter", "📊 Адаптер содержит: $count элементов")
        return count
    }

    override fun submitList(list: List<Country>?) {
        Log.d("CountryAdapter", "🔄 submitList вызван с ${list?.size ?: 0} элементами")
        super.submitList(list)

        if (list != null) {
            Log.d("CountryAdapter", "📋 Первые 5 элементов: ${list.take(5).joinToString { it.name }}")
        }
    }

    inner class CountryViewHolder(
        private val binding: ItemCountryBinding,
        private val onItemClick: (Country) -> Unit,
        private val onFavoriteClick: (Country) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }

            binding.ivFavorite.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onFavoriteClick(getItem(position))
                }
            }
        }

        fun bind(country: Country) {
            with(binding) {
                // Загружаем флаг
                Glide.with(itemView)
                    .load(country.flagUrl)
                    .placeholder(R.drawable.ic_flag_placeholder)
                    .error(R.drawable.ic_error)
                    .into(ivFlag)

                tvCountryName.text = country.name
                tvCapital.text = country.capital ?: "No capital"
                tvRegion.text = country.region
                tvPopulation.text = country.formatPopulation()

                // Устанавливаем иконку избранного
                updateFavoriteIcon(country.isFavorite)
            }
        }

        // ⭐ НОВЫЙ метод для обновления только иконки
        fun updateFavoriteIcon(isFavorite: Boolean) {
            val iconRes = if (isFavorite) {
                R.drawable.ic_favorite_filled
            } else {
                R.drawable.ic_favorite_border
            }
            binding.ivFavorite.setImageResource(iconRes)
        }
    }

    class CountryDiffCallback : DiffUtil.ItemCallback<Country>() {
        override fun areItemsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Country, newItem: Country): Boolean {
            // Сравниваем все поля КРОМЕ isFavorite
            return oldItem.name == newItem.name &&
                    oldItem.capital == newItem.capital &&
                    oldItem.population == newItem.population &&
                    oldItem.region == newItem.region &&
                    oldItem.flagUrl == newItem.flagUrl &&
                    oldItem.currency == newItem.currency &&
                    oldItem.languages == newItem.languages &&
                    oldItem.officialName == newItem.officialName &&
                    oldItem.subregion == newItem.subregion
        }

        override fun getChangePayload(oldItem: Country, newItem: Country): Any? {
            return if (oldItem.isFavorite != newItem.isFavorite) {
                // Возвращаем true если изменилось только isFavorite
                true
            } else {
                null
            }
        }
    }
}