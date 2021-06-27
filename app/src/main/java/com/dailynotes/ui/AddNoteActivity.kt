package com.dailynotes.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dailynotes.R
import com.dailynotes.databinding.ActivityAddNotesBinding
import com.dailynotes.util.helper.AppController
import com.dailynotes.util.helper.CommonMethods
import com.dailynotes.util.helper.ConstantUtility
import com.dailynotes.util.model.Notes
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.toolbar_common.*


class AddNoteActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddNotesBinding
    var db: FirebaseFirestore? = null

    var status=""
   lateinit var notes:Notes
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = FirebaseFirestore.getInstance()

        status=intent.getStringExtra(ConstantUtility.STATUS)!!

        iv_back.setOnClickListener { onBackPressed() }

        iv_update.visibility=View.VISIBLE
        iv_back.visibility=View.VISIBLE
        if (status.equals(ConstantUtility.ADD)){
            tv_title.text=getString(R.string.add_notes)

        }else if (status.equals(ConstantUtility.UPDATE)){
            notes=intent.getSerializableExtra(ConstantUtility.DATA_POSITION) as Notes

            tv_title.text=getString(R.string.update_notes)

            binding.etName.setText(notes.data)
        }


    }




    fun onUpdateClick(view: View){

        if (status.equals(ConstantUtility.ADD)){
            if (binding.etName.text.toString().trim().isEmpty()){
                Toast.makeText(this, "Please enter note", Toast.LENGTH_SHORT).show()
            }else{
                add()
            }
        }else if (status.equals(ConstantUtility.UPDATE)){
            if (binding.etName.text.toString().trim().isEmpty()){
                Toast.makeText(this, "Please enter note", Toast.LENGTH_SHORT).show()
            }else{
                update()
            }
        }




    }

    fun add(){
        CommonMethods.showProgress(this)
        var note=   Notes(
                System.currentTimeMillis().toString(),
                System.currentTimeMillis().toString(),
                binding.etName.text.toString().trim()
        )

        db!!.collection(ConstantUtility.TABLE_NAME_USER)
                .document(AppController.getInstance().getString(ConstantUtility.USER_ID)).collection(ConstantUtility.TABLE_NAME_NOTES)
                .document(note.id)
                .set(note).addOnSuccessListener(OnSuccessListener<Void?> {
                    // getUserData(AppController.getInstance().getString(ConstantUtility.USER_ID))

                    CommonMethods.hideProgress()
                    onBackPressed()
                    Log.e("Success==", "Success")
//                    Toast.makeText(
//                            baseContext, "Update Done",
//                            Toast.LENGTH_SHORT
//                    ).show()
                })
                .addOnFailureListener(OnFailureListener { e ->
                    CommonMethods.hideProgress()
                    Toast.makeText(this, "Failed to add", Toast.LENGTH_SHORT).show()

                    Log.e("Failure", e.toString()) })
    }

    fun update(){

        CommonMethods.showProgress(this)
        var hashMap=HashMap<String,Any>()
        hashMap.put(ConstantUtility.NOTES_ID,notes.id)
        hashMap.put(ConstantUtility.NOTES_DATE,System.currentTimeMillis().toString())
        hashMap.put(ConstantUtility.NOTES_DATA,binding.etName.text.toString().toString())



        db!!.collection(ConstantUtility.TABLE_NAME_USER)
                .document(AppController.getInstance().getString(ConstantUtility.USER_ID)).collection(ConstantUtility.TABLE_NAME_NOTES)
                .document(notes.id)
                .update(hashMap)
                .addOnSuccessListener {

                    CommonMethods.hideProgress()
                    onBackPressed()
//                Toast.makeText(
//                    baseContext, "Update Done",
//                    Toast.LENGTH_SHORT
//                ).show()
                }
                .addOnFailureListener {
                    CommonMethods.hideProgress()

                    Toast.makeText(this, "Failed update", Toast.LENGTH_SHORT).show()
                }
    }
}