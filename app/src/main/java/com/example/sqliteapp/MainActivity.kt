package com.example.sqliteapp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sqliteapp.db.DatabaseOpenHelper
import com.example.sqliteapp.models.User
import com.example.sqliteapp.ui.theme.SQLiteAppTheme


class MainActivity : ComponentActivity() {
    private lateinit var dbHelper: DatabaseOpenHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        dbHelper = DatabaseOpenHelper(this)
        setContent {
            addUser(this, dbHelper)
        }
    }
}

@Composable
fun addUser(context: Context, dbHelper: DatabaseOpenHelper) {
    val genderOptions = listOf("Male", "Female", "Other")
    var expanded by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var users by remember { mutableStateOf(dbHelper.getAllUsers()) }

    Column (modifier = Modifier.padding(50.dp)) {
        TextField(value = name, onValueChange = {name = it}, label = { Text("Name") })
        Spacer(Modifier.height(16.dp))
        TextField(value = lastname, onValueChange = {lastname = it}, label = { Text("Lastname") })
        Spacer(Modifier.height(16.dp))
        TextField(value = age, onValueChange = {age = it}, label = { Text("Age") })
        Spacer(Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = gender,
                onValueChange = {},
                readOnly = true,
                label = { Text("Gender") },
                modifier = Modifier.width(280.dp),
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Expand Menu"
                        )
                    }
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                genderOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            gender = selectionOption
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        TextField(value = phone, onValueChange = {phone = it}, label = { Text("Phone") })
        Spacer(Modifier.height(16.dp))
        TextField(value = email, onValueChange = {email = it}, label = { Text("Email") })
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            val user: User = User(
                name = name,
                lastname = lastname,
                age = age.toIntOrNull() ?: 0,
                gender = gender,
                phone = phone,
                email = email
            )
            if (dbHelper.insertUser(user)) {
                Toast.makeText(
                    context,
                    "User created successfully",
                    Toast.LENGTH_LONG
                ).show()
                users = dbHelper.getAllUsers()
            } else {
                Toast.makeText(
                    context,
                    "Error creating user",
                    Toast.LENGTH_LONG
                ).show()
            }
        }) {
            Text("Insert User")
        }
        Spacer(Modifier.height(16.dp))
        LazyColumn (modifier = Modifier.fillMaxSize()) {
            items(users) { user ->
                userRow(user)
            }
        }
    }
}

@Composable
fun userRow(user: User) {
    Column (modifier = Modifier.padding(8.dp).fillMaxSize()) {
        Text( text = "Name: ${user.name}" )
        Text( text = "Lastname: ${user.lastname}" )
        Text( text = "Age: ${user.age}" )
        Text( text = "Gender: ${user.gender}" )
        Text( text = "Phone: ${user.phone}" )
        Text( text = "Email: ${user.email}" )
        Spacer(Modifier.height(8.dp))
    }
}
