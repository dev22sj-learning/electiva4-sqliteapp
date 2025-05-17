package com.example.sqliteapp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
            AddUser(this, dbHelper)
        }
    }
}

@Composable
fun AddUser(context: Context, dbHelper: DatabaseOpenHelper) {
    val genderOptions = listOf("Male", "Female", "Other")
    var expanded by remember { mutableStateOf(false) }

    var editingUserId by remember { mutableStateOf<Int?>(null) }

    var name by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var users by remember { mutableStateOf(dbHelper.getAllUsers()) }

    fun clearFields() {
        name = ""
        lastname = ""
        age = ""
        gender = ""
        phone = ""
        email = ""
    }

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
            if (editingUserId != null) {
                val updatedUser = user.copy(id = editingUserId)
                if (dbHelper.updateUser(updatedUser)) {
                    showLengthToast(context,"User updated successfully")
                    editingUserId = null
                    clearFields()
                } else {
                    showLengthToast(context,"Error editing user")
                }
            } else {
                if (dbHelper.insertUser(user)) {
                    showLengthToast(context,"User created successfully")
                    clearFields()
                } else {
                    showLengthToast(context,"Error creating user")
                }
            }
            users = dbHelper.getAllUsers()
        }) {
            Text(
                text = if (editingUserId == null) "Insert User" else "Update User"
            )
        }
        Spacer(Modifier.height(16.dp))
        LazyColumn (modifier = Modifier.fillMaxSize()) {
            items(users) { user ->
                UserRow(user = user,
                    onEdit = {
                        editingUserId = user.id
                        name = user.name
                        lastname = user.lastname
                        age = (user.age ?: 0).toString()
                        gender = user.gender
                        phone = user.phone
                        email = user.email
                    },
                    onDelete = {
                        if (user.id != null && dbHelper.deleteUser(user.id)) {
                            editingUserId = null
                            users = dbHelper.getAllUsers()
                            showLengthToast(context,"User deleted successfully")
                        } else {
                            showLengthToast(context,"Error deleting user")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun UserRow(user: User, onEdit: () -> Unit, onDelete: () -> Unit) {
    Column (modifier = Modifier.padding(8.dp).fillMaxSize()) {
        Text( text = "Name: ${user.name}" )
        Text( text = "Lastname: ${user.lastname}" )
        Text( text = "Age: ${user.age}" )
        Text( text = "Gender: ${user.gender}" )
        Text( text = "Phone: ${user.phone}" )
        Text( text = "Email: ${user.email}" )
        Row {
            Button(onClick = onEdit) {
                Text("Edit")
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onDelete) {
                Text("Delete")
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

fun showLengthToast(context: Context, message: String) {
    Toast.makeText(
        context,
        message,
        Toast.LENGTH_LONG
    ).show()
}
