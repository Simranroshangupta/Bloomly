package com.example.cutetodo

import android.os.Bundle
import android.widget.Toast
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.cutetodo.ui.theme.CuteTodoTheme
import com.example.cutetodo.ui.theme.BoxColorsLight
import com.example.cutetodo.ui.theme.BoxColorsDark
import com.example.cutetodo.ui.theme.LightGradientColors
import com.example.cutetodo.ui.theme.DarkGradientColors
import androidx.compose.material3.LinearProgressIndicator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.TextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Scaffold
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.BarChart
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val Quicksand = FontFamily(
    Font(R.font.quicksand_regular, FontWeight.Normal),
    Font(R.font.quicksand_bold, FontWeight.Bold),
)

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Stats : Screen("stats", "Stats", Icons.Default.BarChart)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemDark = isSystemInDarkTheme()
            var isDarkMode by remember { mutableStateOf(systemDark) }

            CuteTodoTheme(darkTheme = isDarkMode) {
                MainScreen(
                    isDark = isDarkMode,
                    onToggleTheme = { isDarkMode = !isDarkMode }
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    isDark: Boolean,
    onToggleTheme: () -> Unit
) {
    val navController = rememberNavController()
    val screens = listOf(Screen.Home, Screen.Stats)
    val context = LocalContext.current
    val dataStore = remember { TodoDataStore(context) }
    val todos by dataStore.getTodoList.collectAsState(initial = emptyList())

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                contentColor = MaterialTheme.colorScheme.primary,
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title, fontFamily = Quicksand, fontWeight = FontWeight.Bold) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                CuteTodoApp(
                    isDark = isDark,
                    onToggleTheme = onToggleTheme,
                    dataStore = dataStore,
                    savedTodos = todos
                )
            }
            composable(Screen.Stats.route) {
                StatisticsScreen(todos = todos, isDark = isDark)
            }
        }
    }
}

@Composable
fun CuteTodoApp(
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    dataStore: TodoDataStore,
    savedTodos: List<TodoItem>
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val boxColors = if (isDark) BoxColorsDark else BoxColorsLight

    // Selection state
    var selectedIndices by remember { mutableStateOf(setOf<Int>()) }
    val isSelectionMode = selectedIndices.isNotEmpty()

    // Sort: Pinned first
    val sortedItems = remember(savedTodos) {
        savedTodos.mapIndexed { index, item -> index to item }
            .sortedByDescending { it.second.isPinned }
    }

    // Pad to 30 items
    val displayList = remember(sortedItems) {
        val list = sortedItems.map { it.first as Int? to it.second as TodoItem? }.toMutableList()
        while (list.size < 30) list.add(null to null)
        list.toList()
    }

    val completedTasks = savedTodos.count { it.isDone }
    val totalTasks = savedTodos.size
    val taskProgress = if (totalTasks == 0) 0f else completedTasks.toFloat() / totalTasks

    var editingIndex by remember { mutableStateOf<Int?>(null) }
    var dialogText by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = if (isDark) DarkGradientColors else LightGradientColors
                    )
                )
                .padding(16.dp)
        ) {
            // --- HEADER OR SELECTION BAR ---
            AnimatedContent(targetState = isSelectionMode, label = "header") { selected ->
                if (selected) {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { selectedIndices = emptySet() }) {
                            Text("Cancel", fontFamily = Quicksand, color = MaterialTheme.colorScheme.primary)
                        }
                        Row {
                            TextButton(onClick = {
                                val newList = savedTodos.toMutableList()
                                val shouldPin = selectedIndices.any { !savedTodos[it].isPinned }
                                selectedIndices.forEach { idx ->
                                    newList[idx] = newList[idx].copy(isPinned = shouldPin)
                                }
                                scope.launch { dataStore.saveTodoList(newList) }
                                selectedIndices = emptySet()
                            }) {
                                Text("📌 Pin/Unpin", fontFamily = Quicksand, color = MaterialTheme.colorScheme.primary)
                            }
                            TextButton(onClick = {
                                val newList = savedTodos.toMutableList()
                                selectedIndices.sortedDescending().forEach { idx ->
                                    newList.removeAt(idx)
                                }
                                scope.launch { dataStore.saveTodoList(newList) }
                                selectedIndices = emptySet()
                                Toast.makeText(context, "Goals deleted 💔", Toast.LENGTH_SHORT).show()
                            }) {
                                Text("🗑️ Delete", fontFamily = Quicksand, color = Color.Red)
                            }
                        }
                    }
                } else {
                    CuteHeader(
                        taskProgress = taskProgress,
                        completedTasks = completedTasks,
                        totalTasks = totalTasks,
                        isDark = isDark,
                        onToggleTheme = onToggleTheme
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(displayList) { gridIndex, pair ->
                    val originalIndex = pair.first
                    val item = pair.second
                    val isSelected = originalIndex != null && selectedIndices.contains(originalIndex)

                    TodoBox(
                        item = item,
                        boxColor = if (isSelected) MaterialTheme.colorScheme.primary else boxColors[gridIndex % boxColors.size],
                        onAdd = {
                            editingIndex = savedTodos.size // Add to end
                            dialogText = ""
                        },
                        onToggleDone = {
                            if (isSelectionMode && originalIndex != null) {
                                selectedIndices = if (isSelected) selectedIndices - originalIndex else selectedIndices + originalIndex
                            } else if (originalIndex != null) {
                                val newList = savedTodos.toMutableList()
                                val isNowDone = !newList[originalIndex].isDone
                                newList[originalIndex] = newList[originalIndex].copy(
                                    isDone = isNowDone,
                                    completedAt = if (isNowDone) System.currentTimeMillis() else null
                                )
                                scope.launch { dataStore.saveTodoList(newList) }
                            }
                        },
                        onEdit = {
                            if (originalIndex != null) {
                                editingIndex = originalIndex
                                dialogText = item?.text ?: ""
                            }
                        },
                        onDelete = {
                            if (originalIndex != null) {
                                selectedIndices = selectedIndices + originalIndex
                            }
                        }
                    )
                }
            }
        }
    }

    if (editingIndex != null) {
        AlertDialog(
            onDismissRequest = { editingIndex = null },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = if (editingIndex!! >= savedTodos.size) "✨ New Goal ✨" else "✏️ Edit Goal",
                    fontFamily = Quicksand,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                OutlinedTextField(
                    value = dialogText,
                    onValueChange = { dialogText = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    textStyle = TextStyle(fontFamily = Quicksand, fontSize = 16.sp),
                    placeholder = { Text("Type your cute goal...", fontFamily = Quicksand) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (dialogText.isNotBlank()) {
                            val newList = savedTodos.toMutableList()
                            if (editingIndex!! < savedTodos.size) {
                                newList[editingIndex!!] = newList[editingIndex!!].copy(text = dialogText.trim())
                            } else {
                                newList.add(TodoItem(text = dialogText.trim()))
                            }
                            scope.launch { dataStore.saveTodoList(newList) }
                        }
                        editingIndex = null
                    }
                ) {
                    Text("Save 💕", fontFamily = Quicksand, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingIndex = null }) {
                    Text("Cancel", fontFamily = Quicksand)
                }
            }
        )
    }
}

@Composable
fun StatisticsScreen(todos: List<TodoItem>, isDark: Boolean) {
    val totalTasks = todos.size
    val completedTasks = todos.count { it.isDone }
    val pendingTasks = totalTasks - completedTasks
    val completionPercentage = if (totalTasks > 0) (completedTasks.toFloat() / totalTasks) else 0f
    val animatedProgress by animateFloatAsState(targetValue = completionPercentage, animationSpec = tween(1000), label = "")

    // Calculate Streak (Consecutive days with at least one task done)
    val streak = remember(todos) {
        val completedDates = todos.filter { it.isDone && it.completedAt != null }
            .map { 
                val cal = Calendar.getInstance()
                cal.timeInMillis = it.completedAt!!
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }
            .distinct()
            .sortedDescending()

        if (completedDates.isEmpty()) 0
        else {
            var count = 0
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            
            val yesterday = today - 86400000L
            
            // If the latest completion wasn't today or yesterday, streak is broken
            if (completedDates.first() < yesterday) 0
            else {
                var expectedDate = completedDates.first()
                for (date in completedDates) {
                    if (date == expectedDate) {
                        count++
                        expectedDate -= 86400000L
                    } else break
                }
                count
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(if (isDark) DarkGradientColors else LightGradientColors))
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📊 Statistics", fontSize = 30.sp, fontWeight = FontWeight.Bold, fontFamily = Quicksand, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
            CircularProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 12.dp,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                strokeCap = StrokeCap.Round
            )
            Text("${(completionPercentage * 100).toInt()}%", fontSize = 32.sp, fontWeight = FontWeight.Bold, fontFamily = Quicksand)
        }
        Spacer(modifier = Modifier.height(32.dp))
        val boxColors = if (isDark) BoxColorsDark else BoxColorsLight
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard("Total", totalTasks.toString(), boxColors[0], Modifier.weight(1f))
            StatCard("Completed", completedTasks.toString(), boxColors[1], Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard("Pending", pendingTasks.toString(), boxColors[2], Modifier.weight(1f))
            StatCard("Streak", "$streak 🔥", boxColors[3], Modifier.weight(1f))
        }
    }
}

@Composable
fun StatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(color.copy(alpha = 0.4f))
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Text(title, fontSize = 14.sp, fontFamily = Quicksand)
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = Quicksand)
        }
    }
}

@Composable
fun CuteHeader(
    taskProgress: Float,
    completedTasks: Int,
    totalTasks: Int,
    isDark: Boolean,
    onToggleTheme: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }
    AnimatedVisibility(visible = isVisible, enter = fadeIn() + slideInVertically()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Surface(onClick = onToggleTheme, shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)) {
                Text(if (isDark) "✨ Light ☀️" else "✨ Dark 🌙", modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp), fontFamily = Quicksand, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Text("🌸 Bloomly 🌸", fontSize = 30.sp, fontWeight = FontWeight.Bold, fontFamily = Quicksand, color = MaterialTheme.colorScheme.primary)
            Text(SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault()).format(Date()), fontFamily = Quicksand, fontSize = 14.sp)
            val animProgress by animateFloatAsState(taskProgress, label = "")
            LinearProgressIndicator(progress = { animProgress }, modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(20.dp)), color = MaterialTheme.colorScheme.primary)
            Text("$completedTasks / $totalTasks Tasks Completed 💜", fontFamily = Quicksand, fontSize = 13.sp)
        }
    }
}

@Composable
fun TodoBox(item: TodoItem?, boxColor: Color, onAdd: () -> Unit, onToggleDone: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val pulseScale by infiniteTransition.animateFloat(1f, 1.1f, infiniteRepeatable(tween(1200), RepeatMode.Reverse), label = "")
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "")
    val backgroundColor by animateColorAsState(if (item?.isDone == true) boxColor.copy(alpha = 0.4f) else boxColor, label = "")

    Box(
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(backgroundColor.copy(alpha = 0.5f))
            .border(1.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), RoundedCornerShape(32.dp))
            .pointerInput(item) {
                detectTapGestures(
                    onPress = { try { isPressed = true; awaitRelease() } finally { isPressed = false } },
                    onTap = { if (item == null) onAdd() else onToggleDone() },
                    onDoubleTap = { if (item != null) onEdit() },
                    onLongPress = { if (item != null) onDelete() }
                )
            }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(targetState = item == null, label = "") { isEmpty ->
            if (isEmpty) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("+", fontFamily = Quicksand, fontSize = 52.sp, modifier = Modifier.graphicsLayer { scaleX = pulseScale; scaleY = pulseScale })
                    Text("Add Goal", fontFamily = Quicksand, fontWeight = FontWeight.Bold)
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (item?.isPinned == true) Text("📌", modifier = Modifier.align(Alignment.End))
                    if (item?.isDone == true) Text("✅", fontSize = 24.sp)
                    Text(item?.text ?: "", fontFamily = Quicksand, textAlign = TextAlign.Center, textDecoration = if (item?.isDone == true) TextDecoration.LineThrough else null)
                }
            }
        }
    }
}
