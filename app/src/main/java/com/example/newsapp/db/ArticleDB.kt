package com.example.newsapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.newsapp.models.Article


@Database(entities = [Article::class], version = 1)
@TypeConverters(Converters::class)// to tell room what converts should be use
abstract class ArticleDB : RoomDatabase() {

    // instance from DAO
    abstract fun getArticleDao(): ArticleDao

    // this block to create single instance from Room to avoid instance woking in many threads
    companion object {

        @Volatile
        //volatile, meaning that writes to this field are immediately made visible to other threads.
        private var instance: ArticleDB? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDB::class.java,
                "article_db.db"
            ).build()
    }
}