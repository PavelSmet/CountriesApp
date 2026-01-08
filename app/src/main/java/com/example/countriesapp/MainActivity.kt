package com.example.countriesapp

import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView  // ← ИМЕННО ЭТОТ ИМПОРТ!
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.countriesapp.databinding.ActivityMainBinding
import com.example.countriesapp.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MainActivity", "🚀 onCreate() начат")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("MainActivity", "✅ Layout загружен")

        // Устанавливаем Toolbar как ActionBar
        setSupportActionBar(binding.toolbar)
        Log.d("MainActivity", "✅ Toolbar установлен")

        // Находим NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        Log.d("MainActivity", "✅ NavController получен")

        // Настраиваем AppBarConfiguration с TOP-LEVEL destinations
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.mainFragment, R.id.favoritesFragment),
            binding.drawerLayout
        )

        Log.d("MainActivity", "✅ AppBarConfiguration создан")
        Log.d("MainActivity", "📋 TopLevelDestinations: ${appBarConfiguration.topLevelDestinations}")

        // Настраиваем ActionBar с Navigation
        setupActionBarWithNavController(navController, appBarConfiguration)
        Log.d("MainActivity", "✅ ActionBar настроен с NavController")

        // ⭐ ВАЖНО: ТОЛЬКО ЭТА СТРОКА для навигации через drawer
        binding.navView.setupWithNavController(navController)
        Log.d("MainActivity", "✅ NavigationView настроен с NavController")

        // Добавляем лисенер для отслеживания навигации
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            Log.d("MainActivity", "🎯 Destination изменен:")
            Log.d("MainActivity", "   Текущий destination ID: ${destination.id}")
            Log.d("MainActivity", "   Текущий destination Label: ${destination.label}")

            // Проверяем стек навигации безопасно
            try {
                // Получаем текущую и предыдущую записи в стеке
                val currentEntry = controller.currentBackStackEntry
                val previousEntry = controller.previousBackStackEntry

                Log.d("MainActivity", "   📊 Текущая запись: ${currentEntry?.destination?.id}")
                Log.d("MainActivity", "   📊 Предыдущая запись: ${previousEntry?.destination?.id}")

                // Пытаемся оценить размер стека
                var stackSize = 0
                var tempEntry = currentEntry
                while (tempEntry != null) {
                    stackSize++
                    tempEntry = controller.getBackStackEntry(tempEntry.destination.id)
                    if (stackSize > 10) break // защита от бесконечного цикла
                }
                Log.d("MainActivity", "   📊 Приблизительный размер стека: $stackSize")

            } catch (e: Exception) {
                Log.d("MainActivity", "   📊 Не удалось проверить стек: ${e.message}")
            }

            // Проверяем конкретные ID и обновляем заголовок Toolbar
            when (destination.id) {
                R.id.mainFragment -> {
                    Log.d("MainActivity", "   📱 Это MainFragment!")
                    // Проверяем фрагменты в NavHost
                    checkFragmentsInNavHost("MainFragment")

                    // Обновляем заголовок Toolbar
                    val region = arguments?.getString("selectedRegion") ?: "All"
                    if (region != "All") {
                        binding.toolbar.title = "Countries - $region"
                        Log.d("MainActivity", "   🏷️ Заголовок обновлен: Countries - $region")
                    } else {
                        binding.toolbar.title = "All Countries"
                        Log.d("MainActivity", "   🏷️ Заголовок обновлен: All Countries")
                    }
                }

                R.id.favoritesFragment -> {
                    Log.d("MainActivity", "   ⭐ Это FavoritesFragment!")
                    // Критично: проверяем, не вложен ли фрагмент
                    checkFragmentsInNavHost("FavoritesFragment")

                    // Проверяем родителя
                    val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                    if (navHost is NavHostFragment) {
                        val currentFragment = navHost.childFragmentManager.primaryNavigationFragment
                        currentFragment?.let { fragment ->
                            fragment.parentFragment?.let { parent ->
                                if (parent is NavHostFragment) {
                                    Log.d(
                                        "MainActivity",
                                        "   ✅ FavoritesFragment имеет правильного родителя: NavHostFragment"
                                    )
                                } else {
                                    Log.w(
                                        "MainActivity",
                                        "   ⚠️ FavoritesFragment имеет неожиданного родителя: ${parent.javaClass.simpleName}"
                                    )
                                }
                            }
                        }
                    }

                    // Обновляем заголовок Toolbar
                    binding.toolbar.title = "Favorite Countries"
                    Log.d("MainActivity", "   🏷️ Заголовок обновлен: Favorite Countries")
                }

                R.id.detailFragment -> {
                    Log.d("MainActivity", "   🔍 Это DetailFragment!")

                    // Обновляем заголовок Toolbar
                    binding.toolbar.title = "Country Details"
                    Log.d("MainActivity", "   🏷️ Заголовок обновлен: Country Details")
                }
            }
        }

        // Для регионов (которые не destinations) обрабатываем отдельно
        setupRegionMenuHandlers()
        Log.d("MainActivity", "✅ Обработчики регионов настроены")

        // Настройка поиска
        setupSearch()
        Log.d("MainActivity", "✅ Поиск настроен")

        // Настройка обработки кнопки "Назад"
        setupOnBackPressedCallback()
        Log.d("MainActivity", "✅ Обработчик кнопки Назад настроен")

        Log.d("MainActivity", "🏁 onCreate() завершен")

        // Проверяем начальное состояние
        val currentDestination = navController.currentDestination
        Log.d("MainActivity", "📊 Текущий destination: ${currentDestination?.id}")
        Log.d("MainActivity", "📊 Текущий label: ${currentDestination?.label}")
    }

    private fun checkFragmentsInNavHost(context: String) {
        try {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
            navHostFragment?.let { navHost ->
                val fragmentManager = navHost.childFragmentManager
                val fragments = fragmentManager.fragments

                Log.d("MainActivity", "   👁️ [$context] Фрагментов в NavHost: ${fragments.size}")

                if (fragments.size > 1) {
                    Log.w(
                        "MainActivity",
                        "   ⚠️ [$context] ПРЕДУПРЕЖДЕНИЕ: Найдено ${fragments.size} фрагментов!"
                    )
                    fragments.forEachIndexed { index, fragment ->
                        Log.w(
                            "MainActivity",
                            "   ⚠️ [$context] Фрагмент $index: ${fragment.javaClass.simpleName}"
                        )
                        Log.w("MainActivity", "   ⚠️ [$context]   Видимый? ${fragment.isVisible}")
                        Log.w("MainActivity", "   ⚠️ [$context]   В стеке? ${fragment.isAdded}")

                        // Проверяем view
                        fragment.view?.let { view ->
                            Log.w(
                                "MainActivity",
                                "   ⚠️ [$context]   View: ${view.javaClass.simpleName}"
                            )
                            Log.w(
                                "MainActivity",
                                "   ⚠️ [$context]   View видимость: ${view.visibility}"
                            )
                        }
                    }
                } else if (fragments.isEmpty()) {
                    Log.w("MainActivity", "   ⚠️ [$context] Нет фрагментов в NavHost!")
                } else {
                    Log.d("MainActivity", "   ✅ [$context] Только один фрагмент - это правильно")
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "   ❌ [$context] Ошибка проверки фрагментов: ${e.message}")
        }
    }

    private fun setupOnBackPressedCallback() {
        Log.d("MainActivity", "🔄 Настройка OnBackPressedCallback")

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("MainActivity", "🔙 Кнопка Назад нажата")
                Log.d(
                    "MainActivity",
                    "🔙 Drawer открыт? ${binding.drawerLayout.isDrawerOpen(binding.navView)}"
                )

                if (binding.drawerLayout.isDrawerOpen(binding.navView)) {
                    Log.d("MainActivity", "🔙 Закрываем drawer")
                    binding.drawerLayout.closeDrawer(binding.navView)
                } else {
                    Log.d("MainActivity", "🔙 Стандартное поведение Назад")
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun setupRegionMenuHandlers() {
        // Часть 1: Логирование NavHost (оставить)
        Log.d("MainActivity", "🔍 Проверка NavHost:")
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        Log.d("MainActivity", "   NavHost найден: ${navHost != null}")
        Log.d("MainActivity", "   NavHost тип: ${navHost?.javaClass?.simpleName}")

        if (navHost is NavHostFragment) {
            Log.d(
                "MainActivity",
                "   Количество фрагментов: ${navHost.childFragmentManager.fragments.size}"
            )
            navHost.childFragmentManager.fragments.forEach { fragment ->
                Log.d("MainActivity", "   Фрагмент: ${fragment.javaClass.simpleName}")
                Log.d("MainActivity", "   View: ${fragment.view}")
                Log.d("MainActivity", "   Добавлен: ${fragment.isAdded}")
                Log.d("MainActivity", "   Видимый: ${fragment.isVisible}")
            }
        }

        // ⭐ Часть 2: Обработка кликов по регионам (ДОБАВИТЬ!)
        val menu = binding.navView.menu

        val regionIds = listOf(
            R.id.all_regions,   // Добавили
            R.id.africa,
            R.id.americas,
            R.id.asia,
            R.id.europe,
            R.id.oceania
        )

        regionIds.forEach { regionId ->
            menu.findItem(regionId)?.setOnMenuItemClickListener { item ->
                handleRegionClick(item.itemId)
                true
            }
        }
    }

    private fun handleRegionClick(regionId: Int) {
        Log.d("MainActivity", "🔄 Обработка клика региона: $regionId")

        val regionName = when (regionId) {
            R.id.all_regions -> "All"
            R.id.africa -> "Africa"
            R.id.americas -> "Americas"
            R.id.asia -> "Asia"
            R.id.europe -> "Europe"
            R.id.oceania -> "Oceania"
            else -> null
        }

        Log.d("MainActivity", "🌍 Выбран регион: $regionName")

        regionName?.let { region ->
            val bundle = Bundle().apply {
                putString("selectedRegion", region)
            }

            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.mainFragment, true)
                .build()

            navController.navigate(R.id.mainFragment, bundle, navOptions)
        }

        binding.drawerLayout.closeDrawer(binding.navView)
    }

    override fun onSupportNavigateUp(): Boolean {
        Log.d("MainActivity", "🔼 onSupportNavigateUp вызван")
        val result = navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
        Log.d("MainActivity", "🔼 Результат navigateUp: $result")
        return result
    }

    private fun setupSearch() {
        // ⭐ ВАЖНО: Используем findViewById с binding.toolbar
        val searchView = binding.toolbar.findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                performSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                performSearch(newText)
                return true
            }
        })
    }

    private fun performSearch(query: String) {
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.childFragmentManager?.fragments?.forEach {
            if (it is MainFragment) {
                it.searchCountries(query)
            }
        }
    }
}