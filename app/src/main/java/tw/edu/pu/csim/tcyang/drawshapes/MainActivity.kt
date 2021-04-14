package tw.edu.pu.csim.tcyang.drawshapes

import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.tensorflow.lite.support.image.TensorImage
import tw.edu.pu.csim.tcyang.drawshapes.ml.Shapes

class MainActivity : AppCompatActivity(),
    View.OnClickListener, View.OnTouchListener{

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn.setOnClickListener(this)
        draw_view.setOnTouchListener(this)

        draw_view.setStrokeWidth(70.0f) //畫筆粗細
        draw_view.setColor(Color.WHITE) //畫筆顏色
        draw_view.setBackgroundColor(Color.BLACK) //背景顏色
    }

    override fun onClick(p0: View?) {
        draw_view.clearCanvas()  //清除繪圖區
    }

    override fun onTouch(p0: View?, event: MotionEvent): Boolean {
        draw_view.onTouchEvent(event)
        if (event.action == MotionEvent.ACTION_UP){
            //Toast.makeText(this, "手指彈起",Toast.LENGTH_SHORT).show()
            classifyDrawing(draw_view.getBitmap())
        }
        return true
    }

    fun classifyDrawing(bitmap:Bitmap) {
        val model = Shapes.newInstance(this)

        // Creates inputs for reference.
        val image = TensorImage.fromBitmap(bitmap)

        // Runs model inference and gets result.
        //val outputs = model.process(image)
        //val probability = outputs.probabilityAsCategoryList
        val outputs = model.process(image)
                .probabilityAsCategoryList.apply {
                    sortByDescending { it.score } // 排序，高匹配率優先
                }.take(2)  //取最高的n個

        var Result:String = ""
        for (output in outputs){
            when (output.label) {
                "circle" -> Result += "圓形"
                "square" -> Result += "正方形"
                "star" -> Result += "星形"
                "triangle" -> Result += "三角形"
            }
            Result += ": " + String.format("%.1f%%", output.score * 100.0f) + "; "
        }

        // Releases model resources if no longer used.
        model.close()

        Toast.makeText(this, Result, Toast.LENGTH_SHORT).show()
    }


}