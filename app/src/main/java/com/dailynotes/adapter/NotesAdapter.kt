package com.dailynotes.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.dailynotes.databinding.RowNotesListBinding
import com.dailynotes.ui.AddNoteActivity
import com.dailynotes.util.helper.AppController
import com.dailynotes.util.helper.CommonMethods
import com.dailynotes.util.helper.ConstantUtility
import com.dailynotes.util.model.Notes
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


class NotesAdapter(var mContext: Context, var listNotes: ArrayList<Notes>) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {


    var db: FirebaseFirestore? = null

    init {
        db = FirebaseFirestore.getInstance()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowNotesListBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.itemBinding.tvTitle.text=listNotes.get(position).data

        holder.itemBinding.tvDate.text=CommonMethods.convertTimestampToDate(
            listNotes.get(position).date.toLong() / 1000, "dd-MM-yyyy hh:mm a"
        ).toUpperCase()

        holder.itemView.setOnClickListener {
            var intent= Intent(mContext, AddNoteActivity::class.java)
            intent.putExtra(ConstantUtility.STATUS, ConstantUtility.UPDATE)
            intent.putExtra(ConstantUtility.DATA_POSITION, listNotes.get(position))
            mContext.startActivity(intent)

        }

       holder.itemView.setOnLongClickListener {
           deleteNote(listNotes.get(position),position)
           return@setOnLongClickListener true;
       }

    }

    fun deleteNote(notes: Notes,pos:Int) {

        val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
        builder.setMessage("Are you sure you want to delete this note?")
        builder.setCancelable(true)
        builder.setPositiveButton("Yes") { dialog, which ->
            dialog.cancel()
            delete(notes.id,pos)
        }


        builder.setNegativeButton("No") { dialog, which ->
            dialog.cancel()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }


    fun delete(notesId:String,pos: Int){
        CommonMethods.showProgress(mContext)
        db!!.collection(ConstantUtility.TABLE_NAME_USER)
            .document(AppController.getInstance().getString(ConstantUtility.USER_ID)).collection(ConstantUtility.TABLE_NAME_NOTES)
            .document(notesId)
            .delete()
            .addOnSuccessListener {
                CommonMethods.hideProgress()
                listNotes.removeAt(pos)
                notifyDataSetChanged()
            }
            .addOnFailureListener {
                CommonMethods.hideProgress()
                Toast.makeText(
                    mContext, "Failed to delete",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun getItemCount(): Int {
        return listNotes.size
    }

    class ViewHolder(itemView: RowNotesListBinding) :
            RecyclerView.ViewHolder(itemView.root) {
        val itemBinding: RowNotesListBinding = itemView
    }

 }