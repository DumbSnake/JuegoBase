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

    // Insertar un nuevo puntaje solo si está en el top 5 y no es un duplicado
    fun insertScore(points: Int) {
        val db = writableDatabase

        // Verifica si el nuevo puntaje ya está en el top 5
        if (isScoreInTop(points, db)) {
            db.close()
            return
        }

        // Obtener el puntaje más bajo en el top 5 actual
        val minTopScore = getLowestTopScore(db)

        // Insertar el puntaje solo si es mayor que el puntaje más bajo en el top 5
        if (minTopScore == null || points > minTopScore) {
            val values = ContentValues().apply {
                put(COLUMN_POINTS, points)
            }
            db.insert(TABLE_NAME, null, values)

            // Llama a la función para eliminar puntajes extras y asegurar que solo queden los 5 mejores
            deleteExtraScores(db)
        }

        db.close()
    }

    // Función para actualizar el puntaje en tiempo real
    fun updateCurrentScore(points: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_POINTS, points)
        }

        // Insertar o actualizar el puntaje actual en la tabla
        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    // Comprueba si el puntaje ya existe en el top 5
    private fun isScoreInTop(points: Int, db: SQLiteDatabase): Boolean {
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_POINTS),
            "$COLUMN_POINTS = ?",
            arrayOf(points.toString()),
            null, null, null,
            "5"
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    // Obtener el puntaje más bajo en el top 5
    private fun getLowestTopScore(db: SQLiteDatabase): Int? {
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_POINTS),
            null, null, null, null,
            "$COLUMN_POINTS DESC",
            "5"
        )

        var lowestTopScore: Int? = null
        cursor.use {
            if (it.moveToLast()) {
                lowestTopScore = it.getInt(it.getColumnIndexOrThrow(COLUMN_POINTS))
            }
        }
        return lowestTopScore
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
            "$COLUMN_POINTS DESC",
            "$limit"
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
