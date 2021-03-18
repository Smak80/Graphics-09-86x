package ru.smak.graphics_09_86x.ui.main

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.smak.graphics_09_86x.R
import ru.smak.graphics_09_86x.ui.graphics.CartesianView

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private var cartesianSystem: CartesianView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Получаем визуальную модель данных, хранящих список точек
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        // Получаем экземпляр компонента, в котором будет отрисовываться декартовая система координат
        cartesianSystem = activity?.findViewById<CartesianView>(R.id.plane)?.apply{
            // Добавляем обработчик события изменения списка точек
            addOnPointsChangedListener {
                viewModel.points.postValue(points.toList())
            }

            // Добавляем обработчик изменения списка точек в визуальной модели
            viewModel.points.observe(this@MainFragment){
                // При изменении визуальной модели обновляем список точек в cartesianSystem
                points = it.toMutableList()
            }
        }

    }

    /**
     * Обработчик события завершения работы фрагмента
     */
    override fun onStop() {
        // Получаем экземпляр класса работы с настройками приложения
        val prefs = activity?.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE)
        prefs?.run {
            // Начинаем редактировать настройки
            edit().run {
                // Сохраняем в настройках количество точек
                putInt(Constants.POINTS_COUNT, viewModel.points.value?.size?:0)
                // Выполняем сохранение списка точек (через которые проводятся красные линии)
                viewModel.points.value?.forEachIndexed() { ind, value ->
                    putFloat(Constants.POINT + ind.toString(), value)
                }
                // Выполняем фактическое сохранение данных
                apply()
            }
        }
        super.onStop()
    }

    /**
     * Метод вызывается при начале работы с фрагментом (например, при запуске приложения или
     * после повотора активности
     */
    override fun onStart() {
        super.onStart()
        // Получаем доступ к найстройкам приложения
        val prefs = activity?.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE)
        prefs?.run {
            // Считываем число сохраненных точек
            val cnt = getInt(Constants.POINTS_COUNT, 0)
            // Считываем сами точки в список
            val lst = MutableList(cnt){
                getFloat(Constants.POINT+it.toString(), 0F)
            }
            // Переносим список точек в визуальную модель. При этом срабатывает обработчик
            // изменения значения в визуальной модели и, в свою очередь, это значение переносится
            // в cartesianSystem
            viewModel.points.postValue(lst.toList())
        }
    }

    // NB: Методы OnStart и OnStop с использованием SharedPreferences
    // являются самодостаточными и могут использоваться
    // не вместе, а вместо ViewModel. Здесь они приведены для демонстрации работы двух этих
    // технологий сохранения данных.
    // Вы можете разделить их использование и сохранять какую-то часть данных только между
    // поворотами с помощью ViewModel (например, нарисованный в данный момент график),
    // а другую часть данных хранить на постоянной основе
    // с помощью SharedPreferences (например, цвет, толщину линий, границы отрезков и т.д.).

}