package com.example.madlab.experiment12

import android.Manifest
import android.content.ContentProviderOperation
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.madlab.experiment12.databinding.FragmentContactsBinding

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                loadDeviceContacts()
            } else {
                Toast.makeText(context, "Read Contacts permission denied", Toast.LENGTH_SHORT).show()
                loadSampleContacts()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLoadContacts.setOnClickListener {
            checkAndLoadContacts()
        }

        binding.btnAddContact.setOnClickListener {
            addNewContactIntent()
        }

        checkAndLoadContacts()
    }

    private fun checkAndLoadContacts() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            loadDeviceContacts()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    private fun loadDeviceContacts() {
        val contactList = mutableListOf<ContactItem>()
        val cursor = requireActivity().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.let {
            val nameIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = if (nameIdx >= 0) it.getString(nameIdx) else "Unknown"
                val number = if (numberIdx >= 0) it.getString(numberIdx) else "No Number"
                contactList.add(ContactItem(name, number))
            }
            it.close()
        }

        if (contactList.isEmpty()) {
            loadSampleContacts()
            Toast.makeText(context, "No device contacts found. Displaying samples.", Toast.LENGTH_SHORT).show()
        } else {
            val adapter = ContactAdapter(requireContext(), contactList)
            binding.lvContacts.adapter = adapter
            Toast.makeText(context, "Loaded ${contactList.size} contacts from Content Provider", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadSampleContacts() {
        val sampleList = listOf(
            ContactItem("College Help Desk", "+91 8001234567"),
            ContactItem("Library Support", "+91 8007654321"),
            ContactItem("IT Technical Lab", "+91 8009876543"),
            ContactItem("Dr. Smith (Advisor)", "+91 9876543210")
        )
        val adapter = ContactAdapter(requireContext(), sampleList)
        binding.lvContacts.adapter = adapter
    }

    private fun addNewContactIntent() {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            type = ContactsContract.Contacts.CONTENT_TYPE
            putExtra(ContactsContract.Intents.Insert.NAME, "Student Support Advisor")
            putExtra(ContactsContract.Intents.Insert.PHONE, "+91 9000011111")
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to open contacts app", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
