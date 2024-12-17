package com.example.lista2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lista2.databinding.FragmentABinding
import com.example.lista2.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment() {
    private lateinit var binding : FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater)


        //retrieve user list
        var users = mutableListOf<User>()

        arguments?.getSerializable("userList")?.let { arg ->
            users = (arg as? ArrayList<User>) ?: mutableListOf()
        }




        Log.d("LoginFragment", "Received users list: ${users.size}, List: $users")



        binding.register.setOnClickListener{
            var u = binding.username.text.toString()
            var p1 = binding.pwd.text.toString()
            var p2 = binding.pwd2.text.toString()

            for (user in users)
                if(user.username == u)
                    return@setOnClickListener

            if(p1 == p2){
                users.add(User(u, p1))

                // Pass user list
                val bundle = Bundle().apply {
                    putSerializable("userList", ArrayList(users))
                }
                findNavController().navigate(R.id.action_registerFragment_to_fragmentA, bundle)
            }
            else{
                return@setOnClickListener
            }




        }



        return binding.root
    }


}