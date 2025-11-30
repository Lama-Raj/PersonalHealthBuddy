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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(navController: NavController) {

    // state for the current text in the input field
    var inputText by remember { mutableStateOf("") }

    // sample static messages shown in the chat list
    val messages = listOf(
        ChatMessage(1, "Hi, I am your health assistant.", isUser = false),
        ChatMessage(2, "Hello!", isUser = true)
    )

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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        // main content under the top bar
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // scrollable list of chat messages
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(messages, key = { it.id }) { message ->
                    ChatBubble(message = message)
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            // row with input field and send button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // text field where user types a message
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type your message...") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                // send icon button on the right side
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            // clears input text when send is pressed
                            inputText = ""
                        }
                    }
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

// draws one chat bubble for a message
@Composable
fun ChatBubble(message: ChatMessage) {

    // blue for user messages
    val userBlue = Color(0xFF0084FF)

    // choose bubble color based on who sent the message
    val bubbleColor = if (message.isUser) {
        userBlue
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    // choose text color for each bubble type
    val textColor = if (message.isUser) {
        Color.White
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    // align user messages to the right, bot messages to the left
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    color = bubbleColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium
            )
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
