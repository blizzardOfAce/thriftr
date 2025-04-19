package com.example.thriftr.utils.components.homescreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.thriftr.viewModel.HomeViewModel

enum class SortFilterMode { SORT, FILTER }

@Composable
fun SortFilterDialog(
    mode: SortFilterMode,
    currentSort: HomeViewModel.SortOption?,
    currentFilters: Set<HomeViewModel.FilterOption>,
    onDismiss: () -> Unit,
    onSortSelected: (HomeViewModel.SortOption?) -> Unit,
    onFilterToggled: (HomeViewModel.FilterOption) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 6.dp,
            modifier = Modifier
                .widthIn(min = 280.dp, max = 400.dp) // Control min/max width
                .wrapContentHeight() // Adjust height based on content
                .padding(12.dp) // Reduce outer padding
        ) {
            Column(
                modifier = Modifier
                    .padding(4.dp) // Adjust inner padding
            ) {
                Text(
                    text = if (mode == SortFilterMode.SORT) "Sort By" else "Filter By",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding( 8.dp)
                )
                when (mode) {
                    SortFilterMode.SORT -> SortOptionsList(currentSort, onSortSelected)
                    SortFilterMode.FILTER -> FilterOptionsList(currentFilters, onFilterToggled)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.align(Alignment.End)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}


@Composable
private fun SortOptionsList(
    currentSort: HomeViewModel.SortOption?,
    onSortSelected: (HomeViewModel.SortOption?) -> Unit
) {
    Column {
        HomeViewModel.SortOption.entries.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSortSelected(option.takeIf { it != currentSort }) }
                    .padding(vertical = 2.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = option == currentSort,
                    onClick = { onSortSelected(option.takeIf { it != currentSort }) }
                )
                Spacer(Modifier.width(8.dp))
                Text(text = option.displayName)
            }
        }
    }
}

@Composable
private fun FilterOptionsList(
    currentFilters: Set<HomeViewModel.FilterOption>,
    onFilterToggled: (HomeViewModel.FilterOption) -> Unit
) {
    Column {
        HomeViewModel.FilterOption.entries.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onFilterToggled(option) }
                    .padding(vertical = 2.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = currentFilters.contains(option),
                    onCheckedChange = { onFilterToggled(option) }
                )
                Spacer(Modifier.width(8.dp))
                Text(text = option.displayName)
            }
        }
    }
}

