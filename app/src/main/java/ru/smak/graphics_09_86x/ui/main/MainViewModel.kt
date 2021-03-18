package ru.smak.graphics_09_86x.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    // Изменяемый, содержащий точки, в которых были тапы пользователя.
    // Данный список будет сохраняться между поворотами экрана
    var points: MutableLiveData<List<Float>> = MutableLiveData(mutableListOf())
}