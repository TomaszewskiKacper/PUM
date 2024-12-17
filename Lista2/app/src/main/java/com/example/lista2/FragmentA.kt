package com.example.lista2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lista2.databinding.FragmentABinding
import java.io.Serializable

data class User(val username : String, val password : String) : Serializable

class FragmentA : Fragment() {
    private lateinit var binding : FragmentABinding

    var users = mutableListOf<User>(
        User("test", "test"),
        User("test2", "test2"),
        User("test1", "test1"),
        User("kacper", "kkk"),
        User("ania", "aaa"),
        );


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentABinding.inflate(inflater)



        // If "userList" is passed, replace the default list with the passed one
        arguments?.getSerializable("userList")?.let { arg ->
            users = (arg as? ArrayList<User>) ?: mutableListOf()
        }
        Log.d("LoginFragment", "Received users list: ${users.size}, List: $users")

        // Pass user list
        val bundle = Bundle().apply {
            putSerializable("userList", ArrayList(users))
        }


        //LOGIN BUTTON
        binding.login.setOnClickListener{
            findNavController().navigate(R.id.action_fragmentA_to_loginFragment, bundle)
        }


        //REGISTER BUTTON
        binding.register.setOnClickListener{
            findNavController().navigate(R.id.action_fragmentA_to_registerFragment, bundle)
        }




        return binding.root
    }


}