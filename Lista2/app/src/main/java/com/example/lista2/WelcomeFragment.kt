package com.example.lista2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lista2.databinding.FragmentABinding
import com.example.lista2.databinding.FragmentWelcomeBinding


class WelcomeFragment : Fragment() {
    private lateinit var binding : FragmentWelcomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWelcomeBinding.inflate(inflater)



        //retrieve user list
        var users = mutableListOf<User>()

        arguments?.getSerializable("userList")?.let { arg ->
            users = (arg as? ArrayList<User>) ?: mutableListOf()
        }
        Log.d("LoginFragment", "Received users list: ${users.size}, List: $users")

        var u = arguments?.getString("User");

        binding.textView4.text = "witaj\n" + u


        // Pass user list
        val bundle = Bundle().apply {
            putSerializable("userList", ArrayList(users))
        }

        binding.button.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_fragmentA, bundle)
        }


        return binding.root
    }


}