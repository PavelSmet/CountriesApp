package com.example.countriesapp.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide // ✅ ДОБАВЬТЕ ЭТОТ ИМПОРТ
import com.example.countriesapp.CountriesApp
import com.example.countriesapp.R // ✅ Этот импорт должен работать
import com.example.countriesapp.databinding.FragmentDetailBinding
import com.example.countriesapp.ui.ViewModelFactory
import kotlinx.coroutines.launch

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val args: DetailFragmentArgs by navArgs()

    // Создаём ViewModel
    private val viewModel: DetailViewModel by viewModels {
        val app = requireActivity().application as CountriesApp
        ViewModelFactory(app.repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("DetailFragment", "onViewCreated - countryId: ${args.countryId}")

        setupObservers()
        setupListeners()

        // Ждем немного чтобы убедиться что все настроено
        binding.root.postDelayed({
            viewModel.loadCountry(args.countryId)
        }, 50)
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.country.collect { country ->
                    if (view == null || !isAdded) return@collect
                    country?.let {
                        Log.d("DetailFragment", "Получена страна: ${it.name}, флаг URL: ${it.flagUrl}")
                        updateUI(it)
                        updateFavoriteIcon(it.isFavorite)
                    }
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collect { isLoading ->
                    if (view == null) return@collect
                    binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        val iconRes = if (isFavorite) {
            R.drawable.ic_favorite_filled
        } else {
            R.drawable.ic_favorite_border
        }
        binding.fabFavorite.setImageResource(iconRes)
    }
    private fun updateUI(country: com.example.countriesapp.data.model.Country) {
        // 1. Загружаем флаг с помощью Glide
        if (!country.flagUrl.isNullOrEmpty()) {
            Glide.with(requireContext())
                .load(country.flagUrl)
                .placeholder(R.drawable.ic_flag_placeholder) // Создайте этот ресурс
                .error(R.drawable.ic_error) // Создайте этот ресурс
                .into(binding.ivFlag) // ✅ ИЗМЕНИТЕ НА ivFlag (как в layout)
        } else {
            // Если URL пустой, показываем placeholder
            binding.ivFlag.setImageResource(R.drawable.ic_flag_placeholder) // ✅ ИЗМЕНИТЕ НА ivFlag
        }

        // 2. Обновляем остальные данные
        binding.tvCountryName.text = country.name
        binding.tvOfficialName.text = country.officialName
        binding.tvCapital.text = "Столица: ${country.capital ?: "Нет"}"
        binding.tvPopulation.text = "Население: ${country.population}"
        binding.tvRegion.text = "Регион: ${country.region}"
        binding.tvSubregion.text = "Субрегион: ${country.subregion ?: "Нет"}"
        binding.tvLanguages.text = "Языки: ${country.languages ?: "Нет данных"}"
        binding.tvCurrency.text = "Валюта: ${country.currency ?: "Нет данных"}"
        updateFavoriteIcon(country.isFavorite)


        // 3. Дополнительные поля (если есть в модели)
        // Площадь
        country.area?.let {
            binding.tvArea.text = "Площадь: ${country.formatArea()}"
            binding.tvArea.visibility = View.VISIBLE
        }

        // Часовые пояса
        country.timezones?.let {
            binding.tvTimezones.text = "Часовые пояса: $it"
            binding.tvTimezones.visibility = View.VISIBLE
        }

        // Континенты
        country.continents?.let {
            binding.tvContinents.text = "Континенты: $it"
            binding.tvContinents.visibility = View.VISIBLE
        }

        // Герб
        country.coatOfArmsUrl?.let { url ->
            binding.ivCoatOfArms.visibility = View.VISIBLE
            Glide.with(requireContext())
                .load(url)
                .into(binding.ivCoatOfArms)
        }

        // Кнопка Google Maps
        country.googleMapsUrl?.let { url ->
            binding.btnMaps.visibility = View.VISIBLE
            binding.btnMaps.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        }
    }

    private fun setupListeners() {
        binding.fabFavorite.setOnClickListener {
            viewModel.toggleFavorite()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}