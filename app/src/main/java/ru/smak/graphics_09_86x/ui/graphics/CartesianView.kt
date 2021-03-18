package ru.smak.graphics_09_86x.ui.graphics

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import ru.smak.graphics.cartesian.CartesianPlane
import java.util.*

class CartesianView(context: Context, attrs: AttributeSet?) : View(context, attrs){

    constructor(context: Context) : this(context, null)

    // Стиль рисования фона
    private val bgPaint = Paint()

    // Стиль рисования координатных осей
    private val fgPaint = Paint()

    // Стиль рисования горизонтальных линий из примера
    private val hlPaint = Paint()

    // Список ординат точек, через которые будут проводиться горизонтальный прямые для примера
    var points: MutableList<Float> = mutableListOf()

    // Список методов-обработчиков событий изменения списка точек
    private val pointsChangeListeners = mutableListOf<()->Unit>()
    // Добавление обработчиков
    fun addOnPointsChangedListener(l: ()->Unit){
        pointsChangeListeners.add(l)
    }
    // Удаление обработчиков
    fun removeOnPointsChangedListener(l: ()->Unit){
        pointsChangeListeners.remove(l)
    }

    // Координатная плоскость
    val plane: CartesianPlane = CartesianPlane(-1F, 1F, -1F, 1F, width, height)

    var xMin : Float
        get() = plane.xMin
        set(value) {
            plane.xMin = value
        }

    var xMax : Float
        get() = plane.xMax
        set(value) {
            plane.xMax = value
        }

    var yMin : Float
        get() = plane.yMin
        set(value) {
            plane.yMin = value
        }

    var yMax : Float
        get() = plane.yMax
        set(value) {
            plane.yMax = value
        }

    init{
        // Параметры фона
        bgPaint.color = 0xfffffff0.toInt()
        // Параметры рисования осей
        fgPaint.color = 0xff0000ff.toInt()
        fgPaint.strokeWidth = 3F
        // параметры рисования горизонтальных линий, проходящих через точки
        // на которые "тапал" пользователь
        hlPaint.color = 0xffff0000.toInt()
        hlPaint.strokeWidth = 5F

        // Обработчик долгого нажатия - выполняем сбор всех ранее установленных точек
        setOnLongClickListener {
            // Очищаем все точки
            points.clear()
            // уведомляем слушателей об изменении точек
            pointsChangeListeners.forEach { it() }
            true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    /**
     * Обработчик события, которое происходит при касании компонента
     * @param event дополнительные сведения о событии, содержащией, в том числе, информацию о координатах "тапа"
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.run {
            // Преобразуем экранную y-координату в декартовую
            val cy = plane.yScr2Crt(y.toInt())
            // и сохраняем в списке точек, через которые будут проводиться прямые
            points.add(cy)
            // уведомляем слушателей об изменении точек
            pointsChangeListeners.forEach { it() }
        }
        // Запрашиваем перерисовку компонента
        invalidate()
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // Обновляем ширину и высоту в декартовой плоскости в соответствии с текущими размерами
        // компонента
        with (plane){
            realWidth = this@CartesianView.width
            realHeight = this@CartesianView.height
        }
        canvas?.apply{
            // Рисуем фон
            drawPaint(bgPaint)
            // Находим экранные координаты точки, являющейся началом декартовой системы координат
            val x = plane.xCrt2Scr(0F)
            val y = plane.yCrt2Scr(0F)
            // Рисуем оси
            drawLine(0F, y.toFloat(), width.toFloat(), y.toFloat(), fgPaint)
            drawLine(x.toFloat(), 0F, x.toFloat(), height.toFloat(), fgPaint)
            // Выполняем отрисовку горизонтальных линий, через точки, в которых были нажатия
            points.forEach {
                drawLine(0F, plane.yCrt2Scr(it).toFloat(), width.toFloat(), plane.yCrt2Scr(it).toFloat(), hlPaint)
            }
        }
    }
}