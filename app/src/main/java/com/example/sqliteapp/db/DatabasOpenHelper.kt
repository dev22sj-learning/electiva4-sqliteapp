package com.example.sqliteapp.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.sqliteapp.models.User
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

class DatabaseOpenHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        // DB
        private const val DATABASE_NAME = "user_database.db"
        private const val DATABASE_VERSION = 1
        // TABLE
        private const val TABLE_NAME = "users"
        // FIELDS
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_LASTNAME = "lastname"
        private const val COLUMN_AGE = "age"
        private const val COLUMN_GENDER = "gender"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_EMAIL = "email"
        // CREATE TABLE STATEMENT
        private const val CREATE_TABLE = """
            CREATE TABLE $TABLE_NAME(
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_LASTNAME TEXT NOT NULL,
                $COLUMN_AGE INTEGER NOT NULL,
                $COLUMN_GENDER TEXT NOT NULL,
                $COLUMN_PHONE TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL
            )
        """

    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertUser(user: User): Boolean {
        val db = writableDatabase
        val values = setContentUserValues(user)
        db.use {
            return try {
                val result = db.insert(TABLE_NAME, null, values);
                result != -1L;
            } catch (e: Exception) {
                Log.e("Database", "Error al registrar usuario: ", e)
                false
            }
        }
    }

    fun getAllUsers(): List<User> {
        val db = readableDatabase
        val users = mutableListOf<User>()
        db.use {
            try {
                val cursor = db.query(
                    TABLE_NAME,
//                    si consultamos siempre todas las columas podemos pasar este parametro como Null
//                    arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_LASTNAME, COLUMN_AGE,
//                        COLUMN_GENDER, COLUMN_PHONE, COLUMN_EMAIL),
                    null, null, null, null, null, null
                )
                // Usamos "use" para cerrar automáticamente el cursor
                cursor.use { c ->
                    while (c.moveToNext()) {
                        users.add(
                            User(
                                id = c.getInt(c.getColumnIndexOrThrow(COLUMN_ID)),
                                name = c.getString(c.getColumnIndexOrThrow(COLUMN_NAME)),
                                lastname = c.getString(c.getColumnIndexOrThrow(COLUMN_LASTNAME)),
                                age = if (c.isNull(c.getColumnIndexOrThrow(COLUMN_AGE)))
                                    null
                                else
                                    c.getInt(c.getColumnIndexOrThrow(COLUMN_AGE)),
                                gender = c.getString(c.getColumnIndexOrThrow(COLUMN_GENDER)),
                                phone = c.getString(c.getColumnIndexOrThrow(COLUMN_PHONE)),
                                email = c.getString(c.getColumnIndexOrThrow(COLUMN_EMAIL))
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("Database", "Error al consultando usuarios: ", e)
            }
            return  users
        }
    }

    fun deleteUser(id: Int): Boolean {
        val db = writableDatabase
        db.use { // ¡Se cierra solo al terminar!
            return try {
                val result = db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString()))
                result > 0
            } catch (e: Exception) {
                Log.e("Database", "Error al borrar usuario: $id", e)
                false
            }
        }
    }

    fun updateUser(user: User): Boolean {
        if (user.id == null) return false;
        val db = writableDatabase
        db.use {
            return try {
                val values = setContentUserValues(user)
                val result = db.update(TABLE_NAME, values, "$COLUMN_ID=?", arrayOf(user.id.toString()))
                result > 0
            } catch (e: Exception) {
                Log.e("Database", "Error al actualizar usuario: ${user.id}", e)
                false
            }
        }
    }

    private fun setContentUserValues(user: User): ContentValues {
        return ContentValues().apply {
            put(COLUMN_NAME, user.name)
            put(COLUMN_LASTNAME, user.lastname)
            put(COLUMN_AGE, user.age ?: 0)
            put(COLUMN_GENDER, user.gender)
            put(COLUMN_PHONE, user.phone)
            put(COLUMN_EMAIL, user.email)
        }
    }
}