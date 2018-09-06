package name.cantanima.chineseremainderclock

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import name.cantanima.chineseremainderclock.CRC_Quiz
import name.cantanima.chineseremainderclock.Chinese_Remainder
import name.cantanima.chineseremainderclock.R
import java.util.*

class Quiz_abTime (
        val context: Chinese_Remainder,
        val d1: Int, val d2: Int
) : CRC_Quiz(context), ABNumberDialogListener
{

    init {
        show_question()
    }

    /**
     * Create a new quiz question.
     * This is the place to start a new dialog, which in its turn should invoke
     * (directly or otherwise) accept_answer().
     * @see .accept_answer
     */
    override fun show_question() {
        val prod = d1*d2
        randomizer = Random()
        val min = randomizer!!.nextInt(100)
        value = min + randomizer!!.nextInt(prod)
        val r1 = value % d1
        val r2 = value % d2
        quiz_dialog = ABNumberDialog(
                context as Activity, this, min, min + prod - 1,
                r1, r2, d1, d2, complete, total
        )
        val qd = quiz_dialog
        val win = quiz_dialog!!.window
        if (win != null) {
            val attr = win.attributes
            attr.alpha = 0.75f
            win.attributes = attr
            qd!!.show()
        }
    }

    /**
     * User has answered quiz question. This function processes the answer.
     * If the quiz is not complete, a new question is generated; otherwise,
     * a result w/a light-hearted comment is displayed in an AlertDialog.
     * @see .show_question
     * @param hr the hour entered by the user
     * @param min the minute entered by the user
     */
    override fun accept_answer(hr: Int, min: Int) {
        // this is not used here
    }

    override fun cancelled() { quiz_cancelled() }

    override fun num_received(n: Int) {
        val activity = context as Activity
        ++complete
        var message: String
        if (n == value) {
            ++correct
            message = activity.getString(R.string.quiz_correct)
        } else {
            message = activity.getString(R.string.quiz_sorry) + " " + value.toString()
        }
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast.show()
        if (complete < total) show_question()
        else {
            if (quiz_dialog!!.isShowing()) quiz_dialog!!.dismiss()
            var quiz_message: String
            var dialog_dismiss: String
            if (correct == total) {
                quiz_message = activity.getString(R.string.quiz_result_great_job)
                dialog_dismiss = activity.getString(R.string.quiz_result_dismiss_dialog_great)
            } else if (correct == 0) {
                quiz_message = activity.getString(R.string.quiz_result_keep_day_job)
                dialog_dismiss = activity.getString(R.string.quiz_result_dismiss_dialog_keep)
            } else {
                quiz_message = activity.getString(R.string.quiz_result_better_luck)
                dialog_dismiss = activity.getString(R.string.quiz_result_dismiss_dialog_better)
            }
            quiz_message += ": " + correct.toString() + "/" + total.toString()
            AlertDialog.Builder(activity).setTitle(activity.getString(R.string.quiz_result_title))
                    .setMessage(quiz_message).setIcon(R.drawable.ic_action_info)
                    .setPositiveButton(
                            dialog_dismiss,
                            DialogInterface.OnClickListener() {
                                dialog: DialogInterface, which: Int -> dialog.dismiss()
                            }
                    )
                    .show()
            quiz_cancelled()
        }
    }

    var quiz_dialog: ABNumberDialog? = null
    var value = 0
    var randomizer : Random? = Random()
    var correct = 0
    var complete = 0
    val total = 5

}

interface ABNumberDialogListener {

    fun cancelled()

    fun num_received(n: Int)

}

class ABNumberDialog(
        val crc_activity: Activity,
        val listener: ABNumberDialogListener,
        val min: Int, val max: Int, val rem_1: Int, val rem_2: Int, val div_1: Int, val div_2: Int,
        val complete: Int, val total: Int
) : Dialog(crc_activity), View.OnClickListener
{

    init { setCanceledOnTouchOutside(false) }

    /**
     * Similar to [Activity.onCreate], you should initialize your dialog
     * in this method, including calling [.setContentView].
     * @param savedInstanceState If this dialog is being reinitialized after a
     * the hosting activity was previously shut down, holds the result from
     * the most recent call to [.onSaveInstanceState], or null if this
     * is the first time.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.quiz_ab_layout)
        val next_button: Button = findViewById(R.id.quiz_accept_button)
        val nb = next_button
        nb.setOnClickListener(this)
        val number_picker : NumberPicker = findViewById(R.id.quiz_ab_number_picker)
        number_picker.minValue = min
        number_picker.maxValue = max
        var update_text : TextView = findViewById(R.id.remainder_a)
        update_text.setText(rem_1.toString())
        update_text = findViewById(R.id.remainder_b)
        update_text.setText(rem_2.toString())
        update_text = findViewById(R.id.divisor_a)
        update_text.setText(div_1.toString())
        update_text = findViewById(R.id.divisor_b)
        update_text.setText(div_2.toString())
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    override fun onClick(v: View?) {
        val number_picker : NumberPicker = findViewById(R.id.quiz_ab_number_picker)
        listener.num_received(number_picker.value)
        dismiss()
    }

    /**
     * Called when the dialog has detected the user's press of the back
     * key.  The default implementation simply cancels the dialog (only if
     * it is cancelable), but you can override this to do whatever you want.
     */
    override fun onBackPressed() {
        dismiss()
        listener.cancelled()
    }
}