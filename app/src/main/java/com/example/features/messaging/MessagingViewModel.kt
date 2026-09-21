package com.example.features.messaging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.ChatMessage
import com.example.core.model.UserRole
import com.example.core.repository.MessageRepository
import com.example.core.repository.mock.AppRepositoryLocator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MessagingUiState(
    val conversationId: String,
    val participantName: String,
    val currentUserRole: UserRole = UserRole.USER,
    val messages: List<ChatMessage> = emptyList(),
    val currentInput: String = "",
    val isParticipantTyping: Boolean = false,
    val showAttachmentDrawer: Boolean = false
)

class MessagingViewModel(
    private val conversationId: String,
    private val participantName: String,
    private val messageRepository: MessageRepository = AppRepositoryLocator.messageRepository,
    private val authRepository: com.example.core.repository.AuthRepository = AppRepositoryLocator.authRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MessagingUiState(
            conversationId = conversationId,
            participantName = participantName,
            currentUserRole = authRepository.getCurrentRole() ?: UserRole.USER
        )
    )
    val uiState: StateFlow<MessagingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.getActiveSession().collect { session ->
                val role = session?.role ?: UserRole.USER
                _uiState.update { it.copy(currentUserRole = role) }
            }
        }
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            messageRepository.getMessages(conversationId).collect { list ->
                _uiState.update { it.copy(messages = list) }
            }
        }
    }

    fun onInputChange(text: String) {
        _uiState.update { it.copy(currentInput = text) }
    }
    
    fun toggleAttachmentDrawer() {
        _uiState.update { it.copy(showAttachmentDrawer = !it.showAttachmentDrawer) }
    }

    fun sendMessage() {
        val text = _uiState.value.currentInput.trim()
        if (text.isBlank()) return

        val role = _uiState.value.currentUserRole
        viewModelScope.launch {
            _uiState.update { it.copy(currentInput = "") }
            
            // Instantly send my message
            messageRepository.sendMessage(
                conversationId = conversationId,
                text = text,
                senderRole = role
            )
            
            // Advanced Polish: Simulate conversational flow from the other side
            if (role == UserRole.USER) {
                delay(1200) // Brief pause
                _uiState.update { it.copy(isParticipantTyping = true) }
                delay(2800) // Simulate typing time
                _uiState.update { it.copy(isParticipantTyping = false) }
                
                // Auto-reply simulating the specialist
                val autoReplies = listOf(
                    "I see. Let's make sure we track that carefully.",
                    "Thank you for sharing that context.",
                    "I've updated your clinical notes accordingly.",
                    "Could you elaborate on that briefly when you have a moment?"
                )
                messageRepository.sendMessage(
                    conversationId = conversationId,
                    text = autoReplies.random(),
                    senderRole = UserRole.SPECIALIST
                )
            }
        }
    }
}
