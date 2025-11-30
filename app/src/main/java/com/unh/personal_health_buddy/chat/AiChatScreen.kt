package com.unh.personal_health_buddy.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    navController: NavController,
    viewModel: AiChatViewModel = viewModel()
) {

    // state from the view model
    val messages = viewModel.messages
    val inputText = viewModel.inputText

    // list state for scrolling
    val listState = rememberLazyListState()

    // scrolls to the newest message when the list size changes
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    // screen scaffold with blue-and-white top app bar
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AI Health Assistant",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // clear button that resets the chat messages
                    IconButton(
                        onClick = { viewModel.clearChat() }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Clear chat"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        // main content column under the top bar
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.03f))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // scrollable list of chat messages
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState
            ) {
                items(messages, key = { it.id }) { message ->
                    ChatBubble(message = message)
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            // input area card with text field and send button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // text field where user types a message
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { viewModel.onInputChange(it) },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type your message...") },
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // send icon button on the right side, enabled only when there is text
                    IconButton(
                        onClick = { viewModel.sendMessage() },
                        enabled = inputText.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send"
                        )
                    }
                }
            }
        }
    }
}

// draws one chat bubble for a message
@Composable
fun ChatBubble(message: ChatMessage) {

    // Blue for user messages
    val userBlue = Color(0xFF0084FF)

    // bubble color depends on sender
    val bubbleColor = if (message.isUser) {
        userBlue
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    // text color depends on bubble type
    val textColor = if (message.isUser) {
        Color.White
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    // bubble corner shape depends on sender
    val bubbleShape = if (message.isUser) {
        RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomEnd = 4.dp,
            bottomStart = 16.dp
        )
    } else {
        RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomEnd = 16.dp,
            bottomStart = 4.dp
        )
    }

    // small label text for who sent the message
    val senderLabel = if (message.isUser) "You" else "Assistant"

    // row decides if bubble is aligned left or right
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
        ) {
            // small sender label above the bubble
            Text(
                text = senderLabel,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(2.dp))

            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .background(
                        color = bubbleColor,
                        shape = bubbleShape
                    )
                    .padding(10.dp)
            ) {
                Column {
                    // main message text
                    Text(
                        text = message.text,
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // small time label under the message
                    Text(
                        text = message.time,
                        color = Color.White.copy(alpha = if (message.isUser) 0.7f else 0.6f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AiChatScreenPreview() {
    PersonalHealthBuddyTheme {
        AiChatScreen(navController = rememberNavController())
    }
}
