import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: String,
    currentUserId: String,
    participantMap: Map<String, String> // userId -> displayName
) {
    val vm = remember(conversationId, currentUserId) {
        ChatViewModel(conversationId, currentUserId)
    }
    val messages by vm.messages.collectAsState()

    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        // auto-scroll to bottom on new message
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Show participant names excluding current user
                    val names = participantMap
                        .filterKeys { it != currentUserId }
                        .values.joinToString(", ")
                    Text(names.ifBlank { "Group chat" })
                }
            )
        },
        bottomBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f)
                        .padding(bottom = 50.dp),
                    placeholder = { Text("Message…") }
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    vm.sendMessage(input.trim())
                    input = ""
                },
                    modifier = Modifier.padding(bottom = 50.dp)
                ) {
                    Text("Send")
                }
                Spacer(Modifier.width(20.dp))
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            state = listState
        ) {
            items(messages, key = { it.id }) { msg ->
                val isMe = msg.senderId == currentUserId
                val senderName = participantMap[msg.senderId] ?: msg.senderId

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(max = 280.dp)
                            .background(
                                color = if (isMe) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(10.dp)
                    ) {
                        if (!isMe) {
                            Text(
                                text = senderName,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(4.dp))
                        }
                        Text(text = msg.text, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
    Log.d("ChatScreen", "Chat screen displayed")
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChatScreenPreview(){
    ChatScreen(
        conversationId = "conversationId",
        currentUserId = "currentUserId",
        participantMap = mapOf("userId1" to "User 1", "userId2" to "User 2")
    )
}
