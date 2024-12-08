package uk.ac.tees.mad.S3269326

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import uk.ac.tees.mad.S3269326.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        mapView = binding.mapView

        // Load Mapbox Map Style
        mapView.getMapboxMap().loadStyleUri("mapbox://styles/mapbox/streets-v11")

        binding.buttonSetLocation.setOnClickListener {
            requestLocationPermissionsAndCaptureLocation()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigateToPixTextActivity()
                }
            }
        )

        val currentUser = mAuth.currentUser
        currentUser?.let {
            binding.textUsername.text = it.email
            loadProfilePic(it.uid)
        }

        return binding.root
    }

    private fun navigateToPixTextActivity() {
        val intent = Intent(requireContext(), PixTextActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun loadProfilePic(userId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
        dbRef.child("profilePicUrl").get().addOnSuccessListener { snapshot ->
            val profilePicUrl = snapshot.value as? String
            if (profilePicUrl != null) {
                Glide.with(requireContext()).load(profilePicUrl).circleCrop().into(binding.profileImageView)
            }
        }
    }

    private fun requestLocationPermissionsAndCaptureLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
        } else {
            captureLocation()
        }
    }

    private fun captureLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                val locationManager = requireContext().getSystemService(android.content.Context.LOCATION_SERVICE) as android.location.LocationManager
                val location = locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER)

                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    // Center the Mapbox Camera on User's Location
                    mapView.getMapboxMap().setCamera(
                        CameraOptions.Builder()
                            .center(Point.fromLngLat(longitude, latitude))
                            .zoom(15.0)
                            .build()
                    )

                    saveLocationToDatabase(latitude, longitude)
                } else {
                    Toast.makeText(requireContext(), "Failed to get location", Toast.LENGTH_SHORT).show()
                }
            } catch (e: SecurityException) {
                Log.e("ProfileFragment", "Location permission issue", e)
                Toast.makeText(requireContext(), "Location permissions not granted", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Permission is not granted; request it
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
        }
    }


    private fun saveLocationToDatabase(latitude: Double, longitude: Double) {
        val userId = mAuth.currentUser?.uid ?: return
        val dbRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

        dbRef.child("location").setValue("$latitude,$longitude").addOnSuccessListener {
            Toast.makeText(requireContext(), "Location saved!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            captureLocation()
        } else {
            Toast.makeText(requireContext(), "Location permission denied!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
    }

}
