import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        firestore = FirebaseFirestore.getInstance()
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val ageEditText = findViewById<EditText>(R.id.ageEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val searchNameEditText = findViewById<EditText>(R.id.searchNameEditText)
        val searchButton = findViewById<Button>(R.id.searchButton)
        val searchResultTextView = findViewById<TextView>(R.id.searchResultTextView)
        // Save user data to Firestore
        saveButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val age = ageEditText.text.toString().toIntOrNull()
            if (name.isNotEmpty() && age != null) {
                saveUser(name, age)
            } else {
                Toast.makeText(this, "Please enter valid details", Toast.LENGTH_SHORT).show()
            }
        }
        // Search for user by name
        searchButton.setOnClickListener {
            val searchName = searchNameEditText.text.toString()
            if (searchName.isNotEmpty()) {
                searchUserByName(searchName, searchResultTextView)
            } else {
                Toast.makeText(this, "Please enter a name to search",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
    // Function to save user data to Firestore
    private fun saveUser(name: String, age: Int) {
        val user = hashMapOf(
            "name" to name,
            "age" to age
        )
        firestore.collection("users")
            .add(user)
            .addOnSuccessListener {
                Toast.makeText(this, "User added successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add user", Toast.LENGTH_SHORT).show()
            }
    }
    // Function to search for user by name
    private fun searchUserByName(name: String, resultTextView: TextView) {
        firestore.collection("users")
            .whereEqualTo("name", name)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    resultTextView.text = "No user found with the name $name"
                } else {
                    for (document in documents) {
                        val userName = document.getString("name")
                        val userAge = document.getLong("age")
                        resultTextView.text = "Name: $userName, Age: $userAge"
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to search for user", Toast.LENGTH_SHORT).show()
            }
    }
}