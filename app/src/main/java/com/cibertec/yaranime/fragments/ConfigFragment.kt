package com.cibertec.yaranime.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import com.cibertec.yaranime.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ConfigFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ConfigFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var linearLayout: LinearLayout
    private lateinit var backgrounPrincipal: LinearLayout
    /*private lateinit var frameLayout: FrameLayout*/

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
        return inflater.inflate(R.layout.fragment_config, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayout = view.findViewById(R.id.configLinearLayout)
        backgrounPrincipal = view.findViewById(R.id.backgroundPrincipal)
        /*frameLayout = view.findViewById(R.id.frameLayoutBackground)*/

        val themeSpinner: Spinner = view.findViewById(R.id.themeSpinner)
        val wallpaperSpinner: Spinner = view.findViewById(R.id.wallpaperSpinner)
        val versionButton: Button = view.findViewById(R.id.versionButton)

        try {
            val packageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            val versionName = packageInfo.versionName
            val versionCode = packageInfo.versionCode

            val versionText = "Versión: $versionName ($versionCode)"
            versionButton.text = versionText
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        val themeOptions = resources.getStringArray(R.array.theme_options)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, themeOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        themeSpinner.adapter = adapter

        themeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedTheme = parent?.getItemAtPosition(position).toString()

                when (selectedTheme) {
                    "Tema Azul" -> linearLayout.setBackgroundResource(R.color.blue)
                    "Tema Rojo" -> linearLayout.setBackgroundResource(R.color.red)
                    "Tema Verde" -> linearLayout.setBackgroundResource(R.color.green)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No se seleccionó ningún tema
            }
        }

        val wallpaperOptions = resources.getStringArray(R.array.wallpaper_options)
        val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, wallpaperOptions)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        wallpaperSpinner.adapter = adapter2

        wallpaperSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedWallpaper = parent?.getItemAtPosition(position).toString()
                when (selectedWallpaper) {
                    "Clouds" -> backgrounPrincipal.setBackgroundResource(R.drawable.clouds)
                    "Rainbow" -> backgrounPrincipal.setBackgroundResource(R.drawable.rainbow)
                    "Tricolor" -> backgrounPrincipal.setBackgroundResource(R.drawable.tricolor)
                    "Purple" -> backgrounPrincipal.setBackgroundResource(R.drawable.purple)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        /*wallpaper.setOnClickListener {
            Toast.makeText(requireContext(), "Hello World", Toast.LENGTH_LONG).show()
            val intent = Intent(requireContext(), MusicFragment::class.java)
            startActivity(intent)
            backgrounPrincipal.setBackgroundResource(R.drawable.yellow)
        }*/


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ConfigFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ConfigFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}