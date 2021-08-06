package org.techtown.wishmatching

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.list_item.*


class HomeFragment : Fragment() {
    private lateinit var listAdapter: ListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment

        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var list: ArrayList<ListData> =
            requireActivity().intent!!.extras!!.get("DataList") as ArrayList<ListData>
        Log.e("FirstFragment", "Data List: ${list}")

        val gridLayoutManager = GridLayoutManager(context, 2)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        listAdapter = ListAdapter(list)
        listView.layoutManager = gridLayoutManager
        listView.adapter = listAdapter

//        listView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL,false)

    }
    // 메뉴 생성
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_homefragment, menu)




    }
    // 메뉴 버튼 클릭시 이벤트 처리
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                // navigate to settings screen
                true
            }
            R.id.action_test -> {
                startActivity(Intent(activity,ProfileActivity::class.java))
                true
            }
            R.id.action_logout -> {
                activity?.let { MySharedPreferences.clearUser(it) }
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
                activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
//    override fun onPrepareOptionsMenu(menu: Menu){
//        super.onPrepareOptionsMenu(menu)
//        val item = menu.findItem(R.id.action_done)
//        item.isVisible = isEditing
//    }


}