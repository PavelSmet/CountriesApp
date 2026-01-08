package com.example.countriesapp.ui.favorites

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.countriesapp.CountriesApp
import com.example.countriesapp.databinding.FragmentFavoritesBinding
import com.example.countriesapp.ui.ViewModelFactory
import com.example.countriesapp.ui.adapter.CountryAdapter
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModels {
        val app = requireActivity().application as CountriesApp
        ViewModelFactory(app.repository)
    }

    private lateinit var adapter: CountryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("FavoritesFragment", "onViewCreated")

        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        adapter = CountryAdapter(
            onItemClick = { country ->
                // Навигация на DetailFragment
                val action = FavoritesFragmentDirections.actionFavoritesFragmentToDetailFragment(country.id)
                findNavController().navigate(action)
            },
            onFavoriteClick = { country ->
                viewModel.toggleFavorite(country.id)
            }
        )

        binding.rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavorites.adapter = adapter

        binding.rvFavorites.itemAnimator = null
    }

    private fun setupObservers() {
        // Для StateFlow используем collect
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoriteCountries.collect { favorites ->
                    Log.d("FavoritesFragment", "Избранные страны: ${favorites.size} шт")
                    adapter.submitList(favorites)

                    // Показываем/скрываем текст "Пусто"
                    if (favorites.isEmpty()) {
                        binding.tvEmpty.visibility = View.VISIBLE
                        binding.tvEmpty.text = "Нет избранных стран"
                    } else {
                        binding.tvEmpty.visibility = View.GONE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collect { isLoading ->
                    binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}