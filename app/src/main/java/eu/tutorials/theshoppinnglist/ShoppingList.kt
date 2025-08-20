package eu.tutorials.theshoppinnglist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// Immutable data class
data class ShoppingItem(
    val id: Int,
    val name: String,
    val quantity: Int,
    val isEditing: Boolean = false,
    val isPurchased: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListApp() {
    var sItems by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var idCounter by remember { mutableStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // Add item button
        Button(
            onClick = {
                itemName = ""
                itemQuantity = "1"
                showDialog = true
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Add Item")
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(sItems) { item ->
                if (item.isEditing) {
                    ShoppingItemEditor(
                        item = item,
                        onEditComplete = { editedName, editedQuantity ->
                            sItems = sItems.map {
                                if (it.id == item.id) {
                                    it.copy(
                                        name = editedName,
                                        quantity = editedQuantity,
                                        isEditing = false
                                    )
                                } else {
                                    it.copy(isEditing = false)
                                }
                            }
                        },
                        onCancel = {
                            sItems = sItems.map { it.copy(isEditing = false) }
                        }
                    )
                } else {
                    ShoppingListItem(
                        item = item,
                        onEditClick = {
                            sItems = sItems.map { it.copy(isEditing = it.id == item.id) }
                        },
                        onDeleteClick = { sItems = sItems - item },
                        onTogglePurchased = {
                            sItems = sItems.map {
                                if (it.id == item.id) it.copy(isPurchased = !it.isPurchased) else it
                            }
                        }
                    )
                }
            }
        }
    }

    // Dialog for adding new item
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Shopping Item") },
            text = {
                Column {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        singleLine = true,
                        label = { Text("Item name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    )

                    OutlinedTextField(
                        value = itemQuantity,
                        onValueChange = { itemQuantity = it },
                        singleLine = true,
                        label = { Text("Quantity") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    )
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            if (itemName.isNotBlank() && itemQuantity.toIntOrNull() != null) {
                                val newItem = ShoppingItem(
                                    id = idCounter++,
                                    name = itemName,
                                    quantity = itemQuantity.toInt()
                                )
                                sItems = sItems + newItem
                                showDialog = false
                            }
                        }
                    ) {
                        Text("Add")
                    }
                    Button(
                        onClick = { showDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}

@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onTogglePurchased: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(1.dp, Color.Gray),
                shape = RoundedCornerShape(12)
            )
            .background(if (item.isPurchased) Color(0xFFE0F7FA) else Color.White)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.name,
                fontWeight = if (item.isPurchased) FontWeight.Light else FontWeight.Bold,
                color = if (item.isPurchased) Color.Gray else Color.Black
            )
            Text("Qty: ${item.quantity}")
        }

        Row {
            IconButton(onClick = onTogglePurchased) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Mark purchased"
                )
            }
            IconButton(onClick = onEditClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun ShoppingItemEditor(
    item: ShoppingItem,
    onEditComplete: (String, Int) -> Unit,
    onCancel: () -> Unit
) {
    var editedName by remember { mutableStateOf(item.name) }
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF9C4))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            BasicTextField(
                value = editedName,
                onValueChange = { editedName = it },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )

            BasicTextField(
                value = editedQuantity,
                onValueChange = { editedQuantity = it },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )
        }
        Column {
            Button(onClick = {
                if (editedName.isNotBlank() && editedQuantity.toIntOrNull() != null) {
                    onEditComplete(editedName, editedQuantity.toInt())
                }
            }) {
                Text("Save")
            }
            Button(onClick = onCancel) {
                Text("Cancel")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingListPreview() {
    ShoppingListApp()
}
