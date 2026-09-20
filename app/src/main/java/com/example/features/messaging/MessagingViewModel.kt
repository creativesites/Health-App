package com.example.features.messaging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.ChatMessage
import com.example.core.model.UserRole
import com.example.core.repository.MessageRepository
import com.example.core.repository.mock.AppRepositoryLocator
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
    val currentInput: String = ""
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

    fun sendMessage() {
        val text = _uiState.value.currentInput.trim()
        if (text.isBlank()) return

        val role = _uiState.value.currentUserRole
        viewModelScope.launch {
            _uiState.update { it.copy(currentInput = "") }
            messageRepository.sendMessage(
                conversationId = conversationId,
                text = text,
                senderRole = role
            )
        }
    }
}
