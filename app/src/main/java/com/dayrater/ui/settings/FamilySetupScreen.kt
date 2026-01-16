package com.dayrater.ui.settings

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dayrater.R
import com.dayrater.domain.model.FamilyMember

/**
 * Family setup screen composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilySetupScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FamilySetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show error in snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(FamilySetupEvent.DismissError)
        }
    }
    
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.family_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Self section
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    SectionHeader(title = stringResource(R.string.family_self_section))
                }
                
                item {
                    uiState.selfMember?.let { self ->
                        FamilyMemberCard(
                            member = self,
                            onEdit = { viewModel.onEvent(FamilySetupEvent.EditMember(self)) },
                            canDelete = false
                        )
                    }
                }
                
                // Spouse section
                item {
                    SectionHeader(title = stringResource(R.string.family_spouse_section))
                }
                
                if (uiState.spouse != null) {
                    item {
                        FamilyMemberCard(
                            member = uiState.spouse!!,
                            onEdit = { viewModel.onEvent(FamilySetupEvent.EditMember(uiState.spouse!!)) },
                            onDelete = { viewModel.onEvent(FamilySetupEvent.ConfirmDelete(uiState.spouse!!)) },
                            canDelete = true
                        )
                    }
                } else {
                    item {
                        AddButton(
                            text = stringResource(R.string.family_add_spouse),
                            onClick = { viewModel.onEvent(FamilySetupEvent.ShowAddSpouseDialog) }
                        )
                    }
                }
                
                // Children section
                item {
                    SectionHeader(title = stringResource(R.string.family_children_section))
                }
                
                items(uiState.children, key = { it.id }) { child ->
                    FamilyMemberCard(
                        member = child,
                        onEdit = { viewModel.onEvent(FamilySetupEvent.EditMember(child)) },
                        onDelete = { viewModel.onEvent(FamilySetupEvent.ConfirmDelete(child)) },
                        canDelete = true
                    )
                }
                
                item {
                    AddButton(
                        text = stringResource(R.string.family_add_child),
                        onClick = { viewModel.onEvent(FamilySetupEvent.ShowAddChildDialog) }
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
    
    // Add spouse dialog
    if (uiState.showAddSpouseDialog) {
        AddMemberDialog(
            title = stringResource(R.string.family_add_spouse),
            onDismiss = { viewModel.onEvent(FamilySetupEvent.DismissDialog) },
            onConfirm = { name -> viewModel.onEvent(FamilySetupEvent.AddSpouse(name)) }
        )
    }
    
    // Add child dialog
    if (uiState.showAddChildDialog) {
        AddMemberDialog(
            title = stringResource(R.string.family_add_child),
            onDismiss = { viewModel.onEvent(FamilySetupEvent.DismissDialog) },
            onConfirm = { name -> viewModel.onEvent(FamilySetupEvent.AddChild(name)) }
        )
    }
    
    // Edit member dialog
    uiState.editingMember?.let { member ->
        EditMemberDialog(
            member = member,
            onDismiss = { viewModel.onEvent(FamilySetupEvent.DismissDialog) },
            onConfirm = { name -> 
                viewModel.onEvent(FamilySetupEvent.UpdateMemberName(member.id, name))
            }
        )
    }
    
    // Delete confirmation dialog
    uiState.deletingMember?.let { member ->
        DeleteConfirmationDialog(
            memberName = member.name,
            onDismiss = { viewModel.onEvent(FamilySetupEvent.CancelDelete) },
            onConfirm = { viewModel.onEvent(FamilySetupEvent.DeleteMember(member.id)) }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun FamilyMemberCard(
    member: FamilyMember,
    onEdit: () -> Unit,
    onDelete: (() -> Unit)? = null,
    canDelete: Boolean = true
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = member.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.action_edit)
                )
            }
            
            if (canDelete && onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.action_delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun AddButton(
    text: String,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}

@Composable
private fun AddMemberDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.family_name_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name) },
                enabled = name.isNotBlank()
            ) {
                Text(stringResource(R.string.action_add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
private fun EditMemberDialog(
    member: FamilyMember,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf(member.name) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.action_edit)) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.family_name_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name) },
                enabled = name.isNotBlank()
            ) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
private fun DeleteConfirmationDialog(
    memberName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.family_delete_confirm, memberName)) },
        text = { Text(stringResource(R.string.family_delete_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.action_delete),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}
