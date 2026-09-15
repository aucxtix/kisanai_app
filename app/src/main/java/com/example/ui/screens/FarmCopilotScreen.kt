package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppStrings
import com.example.presentation.theme.*
import kotlinx.coroutines.launch

// Function to parse basic markdown bold (**text**)
fun parseMarkdownToAnnotatedString(text: String, isUser: Boolean, kisanCharcoal: Color): androidx.compose.ui.text.AnnotatedString {
    return buildAnnotatedString {
        val parts = text.split("**")
        var isBold = false
        for (part in parts) {
            if (isBold) {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = if (isUser) Color.White else kisanCharcoal)) {
                    append(part)
                }
            } else {
                append(part)
            }
            isBold = !isBold
        }
    }
}

data class CopilotMessage(
    val id: String,
    val sender: String, // "farmer" or "kissan_ai"
    val text: String,
    val timestamp: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmCopilotScreen(
    strings: AppStrings,
    onAskCopilot: (String, (String) -> Unit) -> Unit = { _, _ -> },
    onBackClick: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var queryText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var messages by remember {
        mutableStateOf(
            listOf(
                CopilotMessage(
                    id = "msg_0",
                    sender = "kissan_ai",
                    text = "Namaste Farmer 🙏 I am your Kissan AI Farm Advisor. I am monitoring your Plot 1 (Tomato) and Plot 2 (Cotton). How can I assist your farming operations today?",
                    timestamp = "Just now"
                )
            )
        )
    }

    val suggestedQuestions = listOf(
        "Meri fasal ki condition kya hai?",
        "Aaj paani du?",
        "Aaj ka mandi bhav kya hai?",
        "Yield kaise improve karu?",
        "Disease ka risk kitna hai?",
        "Kal kya karna hai?"
    )

    fun sendQuestion(prompt: String) {
        if (prompt.isBlank() || isLoading) return
        val userMsg = CopilotMessage(
            id = "usr_${System.currentTimeMillis()}",
            sender = "farmer",
            text = prompt,
            timestamp = "Just now"
        )
        messages = messages + userMsg
        queryText = ""
        isLoading = true

        coroutineScope.launch {
            listState.animateScrollToItem(messages.size - 1)
        }

        onAskCopilot(prompt) { answer ->
            val aiMsg = CopilotMessage(
                id = "ai_${System.currentTimeMillis()}",
                sender = "kissan_ai",
                text = answer,
                timestamp = "Just now"
            )
            messages = messages + aiMsg
            isLoading = false
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "AI Farm Copilot",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = KisanCharcoal
                        )
                        Text(
                            text = "On-Device & Cloud Agricultural Intelligence",
                            fontSize = 11.sp,
                            color = KisanMutedSage
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = KisanCharcoal)
                    }
                },
                actions = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = KisanEmerald)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = KisanWarmIvory)
            )
        },
        containerColor = KisanWarmIvory,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("farm_copilot_screen")
        ) {
            // Chat Message List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { msg ->
                    val isUser = msg.sender == "farmer"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        if (!isUser) {
                            Surface(
                                shape = CircleShape,
                                color = KisanEmerald,
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(top = 2.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Psychology, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Card(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isUser) 16.dp else 4.dp,
                                bottomEnd = if (isUser) 4.dp else 16.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUser) KisanDeepForest else Color.White
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.widthIn(max = 290.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = parseMarkdownToAnnotatedString(msg.text, isUser, KisanCharcoal),
                                    fontSize = 13.sp,
                                    color = if (isUser) Color.White else KisanCharcoal,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = msg.timestamp,
                                    fontSize = 9.sp,
                                    color = if (isUser) KisanEmeraldLight else KisanMutedSage,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                    }
                }

                if (isLoading) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 40.dp, top = 4.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = KisanEmerald, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Kissan AI is analyzing farm sensors and crop models...", fontSize = 11.sp, color = KisanMutedSage)
                        }
                    }
                }
            }

            // Quick Suggestion Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(suggestedQuestions) { q ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, KisanEmerald.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { sendQuestion(q) }
                    ) {
                        Text(
                            text = q,
                            fontSize = 11.sp,
                            color = KisanDeepForest,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Input Bar
            Surface(
                color = Color.White,
                tonalElevation = 3.dp,
                modifier = Modifier.fillMaxWidth().padding(bottom = 60.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = queryText,
                        onValueChange = { queryText = it },
                        placeholder = { Text("Ask anything about your crops, irrigation, mandi...", fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("copilot_text_input"),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC),
                            focusedBorderColor = KisanEmerald,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Voice Input Button
                    IconButton(
                        onClick = {
                            sendQuestion("Aaj paani du?")
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFF1F5F9), CircleShape)
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = "Voice Input", tint = KisanDeepForest, modifier = Modifier.size(20.dp))
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Send Button
                    IconButton(
                        onClick = { sendQuestion(queryText) },
                        enabled = queryText.isNotBlank() && !isLoading,
                        modifier = Modifier
                            .size(40.dp)
                            .background(if (queryText.isNotBlank() && !isLoading) KisanEmerald else Color(0xFFE2E8F0), CircleShape)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
