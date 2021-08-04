package org.techtown.wishmatching

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.list_item.*


class HomeFragment : Fragment() {
    private lateinit var listAdapter:ListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var list: ArrayList<ListData> = requireActivity().intent!!.extras!!.get("DataList") as ArrayList<ListData>
        Log.e("FirstFragment","Data List: ${list}")

        val gridLayoutManager = GridLayoutManager(context,2)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        listAdapter = ListAdapter(list)
        listView.layoutManager = gridLayoutManager
        listView.adapter = listAdapter

//        listView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL,false)



    }
    fun onClick(v:View?) {
        when(v?.id) {
            R.id.btn_like -> {
                btn_like.setImageResource(R.drawable.btn_clicked_heart)
            }
        }
    }

}