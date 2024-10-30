package com.example.prueba

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Clase para manejar la base de datos
class GameDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_POINTS INTEGER,
                $COLUMN_TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Insertar un nuevo puntaje y mantener solo los 5 mejores
    fun insertScore(points: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_POINTS, points)
        }
        db.insert(TABLE_NAME, null, values)

        // Llama a la función para eliminar puntajes extras y asegurar que solo queden los 5 mejores
        deleteExtraScores(db)
        db.close()
    }

    // Elimina los puntajes adicionales, manteniendo solo los 5 mejores
    private fun deleteExtraScores(db: SQLiteDatabase) {
        val excessScoresQuery = """
        DELETE FROM $TABLE_NAME 
        WHERE $COLUMN_ID NOT IN (
            SELECT $COLUMN_ID 
            FROM $TABLE_NAME 
            ORDER BY $COLUMN_POINTS DESC 
            LIMIT 5
        )
    """.trimIndent()
        db.execSQL(excessScoresQuery)
    }

    // Obtener los 5 mejores puntajes
    fun getTopScores(limit: Int = 5): List<Int> {
        val scores = mutableListOf<Int>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_POINTS),
            null, null, null, null,
            "$COLUMN_POINTS DESC",  // Ordena los puntajes de mayor a menor
            "$limit"                // Limita a los 5 primeros resultados
        )

        cursor.use {
            while (it.moveToNext()) {
                val score = it.getInt(it.getColumnIndexOrThrow(COLUMN_POINTS))
                scores.add(score)
            }
        }
        db.close()
        return scores
    }

    companion object {
        private const val DATABASE_NAME = "game_db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "Score"
        const val COLUMN_ID = "id"
        const val COLUMN_POINTS = "points"
        const val COLUMN_TIMESTAMP = "timestamp"
    }
}
