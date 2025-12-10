package com.example.countriesapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.countriesapp.databinding.FragmentMainBinding
import com.example.countriesapp.ui.adapter.CountryAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: CountryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupListeners()

        // Загружаем данные
        viewModel.observeCountries()
    }

    private fun setupRecyclerView() {
        adapter = CountryAdapter(
            onItemClick = { country ->
                // TODO: Переход на детальный экран
                // findNavController().navigate(MainFragmentDirections.actionMainFragmentToDetailFragment(country.id))
            },
            onFavoriteClick = { country ->
                viewModel.toggleFavorite(country.id)
            }
        )

        binding.rvCountries.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCountries.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.countries.observe(viewLifecycleOwner) { countries ->
            adapter.submitList(countries)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                // TODO: Показать Snackbar с ошибкой
                // Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun setupListeners() {
        binding.fabRefresh.setOnClickListener {
            viewModel.loadCountries()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}