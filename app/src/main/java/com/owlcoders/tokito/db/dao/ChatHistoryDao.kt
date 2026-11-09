package com.owlcoders.tokito.db.dao

import androidx.room.*
import com.owlcoders.tokito.db.entities.ChatHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatHistoryDao {
    @Query("SELECT * FROM chat_history ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatHistoryEntity>>

    @Query("SELECT * FROM chat_history ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMessages(limit: Int = 50): List<ChatHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatHistoryEntity): Long

    @Delete
    suspend fun deleteMessage(message: ChatHistoryEntity)

    @Query("DELETE FROM chat_history")
    suspend fun deleteAllMessages()

    @Query("SELECT COUNT(*) FROM chat_history")
    suspend fun getMessageCount(): Int
}
