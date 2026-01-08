package com.example.countriesapp.ui.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.countriesapp.CountriesApp
import com.example.countriesapp.R
import com.example.countriesapp.databinding.FragmentMainBinding
import com.example.countriesapp.ui.ViewModelFactory
import com.example.countriesapp.ui.adapter.CountryAdapter

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels {
        val app = requireActivity().application as CountriesApp
        ViewModelFactory(app.repository)
    }

    private lateinit var adapter: CountryAdapter
    private var currentMenu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        Log.d("MainFragment", "📱 onCreateView - разметка создана")
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("MainFragment", "🗑️ onDestroyView - очищаю binding")
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("MainFragment", "🏁 onViewCreated - начинаю настройку UI")
        Log.d("MainFragment", "📦 Аргументы: ${arguments}")
        Log.d("MainFragment", "📦 selectedRegion из аргументов: ${arguments?.getString("selectedRegion")}")

        setupRecyclerView()
        setupObservers()
        setupListeners()

        viewModel.startObservingCountries()
        viewModel.loadCountriesFromApi()

        val selectedRegion = arguments?.getString("selectedRegion") ?: "All"
        Log.d("MainFragment", "🌍 Получен регион из аргументов: $selectedRegion")

        if (selectedRegion != "All") {
            Log.d("MainFragment", "🎯 Применяю фильтр по региону: $selectedRegion")
            viewModel.filterByRegion(selectedRegion)
        } else {
            Log.d("MainFragment", "🎯 Показываю все страны (без фильтра)")
        }

        // Применяем сохраненную сортировку
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.applySavedSort()  // ← ЭТОТ МЕТОД ТЕПЕРЬ В ViewModel!
        }, 500)
    }

    // ==================== МЕНЮ СОРТИРОВКИ ====================
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sort, menu)
        super.onCreateOptionsMenu(menu, inflater)

        currentMenu = menu
        updateMenuSelection()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val result = when (item.itemId) {
            R.id.sort_name_asc -> {
                viewModel.sortByName(ascending = true)
                showSortToast("Сортировка: А-Я")
                true
            }
            R.id.sort_name_desc -> {
                viewModel.sortByName(ascending = false)
                showSortToast("Сортировка: Я-А")
                true
            }
            R.id.sort_population_asc -> {
                viewModel.sortByPopulation(ascending = true)
                showSortToast("Сортировка: население ↑")
                true
            }
            R.id.sort_population_desc -> {
                viewModel.sortByPopulation(ascending = false)
                showSortToast("Сортировка: население ↓")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

        updateMenuSelection()
        return result
    }

    private fun showSortToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    // ==================== НАСТРОЙКА UI ====================
    private fun setupRecyclerView() {
        Log.d("MainFragment", "🔄 Настраиваю RecyclerView")

        adapter = CountryAdapter(
            onItemClick = { country ->
                Log.d("MainFragment", "🎯 Нажата страна: ${country.name}")
                val action = MainFragmentDirections.actionMainFragmentToDetailFragment(country.id)
                findNavController().navigate(action)
            },
            onFavoriteClick = { country ->
                Log.d("MainFragment", "❤️ Нажато сердечко: ${country.name}")
                viewModel.toggleFavorite(country.id)
                Toast.makeText(
                    requireContext(),
                    "Избранное обновлено: ${country.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        binding.rvCountries.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCountries.adapter = adapter
        Log.d("MainFragment", "✅ RecyclerView настроен")
    }

    private fun setupObservers() {
        Log.d("MainFragment", "👀 Настраиваю наблюдателей за ViewModel")

        // 1. Список стран
        viewModel.countries.observe(viewLifecycleOwner) { countries ->
            Log.d("MainFragment", "📱 Получено стран в UI: ${countries.size}")
            adapter.submitList(countries)
        }

        // 2. Тип сортировки
        viewModel.currentSortType.observe(viewLifecycleOwner) { sortType ->
            updateMenuSelection()
        }

        // 3. Прокрутка в начало
        viewModel.shouldScrollToTop.observe(viewLifecycleOwner) { shouldScroll ->
            if (shouldScroll && adapter.itemCount > 0) {
                binding.rvCountries.scrollToPosition(0)
                viewModel.resetScrollFlag()  // ← ЭТОТ МЕТОД ТЕПЕРЬ В ViewModel!
            } else if (shouldScroll) {
                viewModel.resetScrollFlag()  // ← ЭТОТ МЕТОД ТЕПЕРЬ В ViewModel!
            }
        }
    }

    private fun setupListeners() {
        Log.d("MainFragment", "🖱️ Настраиваю обработчики кликов")

        binding.fabRefresh.setOnClickListener {
            Log.d("MainFragment", "🔁 Нажата кнопка обновления")
            viewModel.loadCountriesFromApi()
        }

        Log.d("MainFragment", "✅ Обработчики настроены")
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================
    fun searchCountries(query: String) {
        Log.d("MainFragment", "🔍 Вызван поиск: '$query'")
        viewModel.searchCountries(query)
    }

    private fun updateMenuSelection() {
        currentMenu?.let { menu ->
            // Снимаем все отметки
            menu.findItem(R.id.sort_name_asc)?.isChecked = false
            menu.findItem(R.id.sort_name_desc)?.isChecked = false
            menu.findItem(R.id.sort_population_asc)?.isChecked = false
            menu.findItem(R.id.sort_population_desc)?.isChecked = false

            // Отмечаем текущий
            when (viewModel.currentSortType.value) {
                MainViewModel.SortType.NAME_ASC ->
                    menu.findItem(R.id.sort_name_asc)?.isChecked = true
                MainViewModel.SortType.NAME_DESC ->
                    menu.findItem(R.id.sort_name_desc)?.isChecked = true
                MainViewModel.SortType.POPULATION_ASC ->
                    menu.findItem(R.id.sort_population_asc)?.isChecked = true
                MainViewModel.SortType.POPULATION_DESC ->
                    menu.findItem(R.id.sort_population_desc)?.isChecked = true
                else -> {}
            }
        }
    }
}