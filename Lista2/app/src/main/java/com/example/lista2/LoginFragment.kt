package com.example.lista2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lista2.databinding.FragmentABinding
import com.example.lista2.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {
    private lateinit var binding : FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater)



        //retrieve user list
        var users = mutableListOf<User>()

        arguments?.getSerializable("userList")?.let { arg ->
            users = (arg as? ArrayList<User>) ?: mutableListOf()
        }
        Log.d("LoginFragment", "Received users list: ${users.size}, List: $users")


        // Pass user list
        val bundle = Bundle().apply {
            putSerializable("userList", ArrayList(users))
        }



        binding.login.setOnClickListener{

            //check if user
            for (u in users){
                Log.d("LoginFragment", u.username)

                if(u.username == binding.login.text.toString())     //find matching username
                    Log.d("LoginFragment", "TTTTT")
                    if(binding.password.text.toString() == u.password){ //matching password
                        bundle.apply {
                            putString("User", u.username)
                            putSerializable("userList", ArrayList(users))
                        }
                        findNavController().navigate(R.id.action_loginFragment_to_welcomeFragment, bundle)
                    }
            }
        }

        binding.register.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment, bundle)
        }




        return binding.root
    }







}