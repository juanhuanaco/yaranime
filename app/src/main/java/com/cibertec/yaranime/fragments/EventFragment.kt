package com.cibertec.yaranime.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.cibertec.yaranime.R
import com.cibertec.yaranime.adapters.EventAdapter
import com.cibertec.yaranime.models.Event
import com.google.android.gms.location.LocationServices
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class EventFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var btnFilterLocation : Button
    private var areEventsFiltered : Boolean = false
    private lateinit var recyclerView:RecyclerView
    private lateinit var events:List<Event>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnFilterLocation = view.findViewById(R.id.buttonFilterLocation)
        btnFilterLocation.setOnClickListener{btnFilterLocationAction()}

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        getEvents()
    }

    fun getEvents() {

        val secondDatabase = FirebaseDatabase.getInstance(FirebaseApp.getInstance("events-database"))
        val ref = secondDatabase.getReference("eventos")
        ref.get().addOnSuccessListener {
            events = it.children.map { it.getValue(Event::class.java)!! }
            recyclerView.adapter = EventAdapter(events)
        }.addOnFailureListener{
            Toast.makeText(requireContext(), "Upss... Hubo un problema al obtener los eventos.", Toast.LENGTH_LONG).show()
        }
    }


    fun updateRecyclerViewAdapter(data: List<Event>){
        recyclerView.adapter = EventAdapter(data)
    }

    fun btnFilterLocationAction(){
        if (areEventsFiltered){
            deleteFiltersForEvents()
            btnFilterLocation.text = "Filtrar por mi Ubicación"
        } else {
            filterEventsByLocation()
            btnFilterLocation.text = "Mostrar todo"
        }
        areEventsFiltered = !areEventsFiltered
    }

    fun deleteFiltersForEvents(){
        updateRecyclerViewAdapter(events)
    }

    fun filterEventsByLocation(){
        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        else{
            var fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationClient.lastLocation.addOnSuccessListener {
                location -> if (location != null){
                    val url = "https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${location.latitude}&lon=${location.longitude}"
                    val request = JsonObjectRequest( Request.Method.GET, url, null,
                        { response ->
                            val state = response.getJSONObject("address")
                            updateRecyclerViewAdapter(events.filter { it.city.contentEquals(state.getString("state")) })
                            Toast.makeText(requireContext(), "Tu ciudad es ${state.getString("state")}", Toast.LENGTH_LONG).show()
                        },
                        {
                            Toast.makeText(requireContext(), "Upss... Hubo un problema al obtener tu ciudad.", Toast.LENGTH_LONG).show()
                        })
                    Volley.newRequestQueue(requireContext()).add(request)

            }
            }.addOnFailureListener{
                Toast.makeText(requireContext(), "Upss... Hubo un problema al obtener tu ubicación.", Toast.LENGTH_LONG).show()
            }

        }

    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EventFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}