package com.example.thriftr.utils.components.homescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thriftr.viewModel.HomeViewModel

@Composable
 fun DealsHeader(
    title: String,
    onSortClick: () -> Unit,
    onFilterClick: () -> Unit,
    sortOption: HomeViewModel.SortOption?,
    filterOptions: Set<HomeViewModel.FilterOption>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            FilterChip(
                text = "Sort",
                icon = Icons.AutoMirrored.Filled.Sort,
                isSelected = sortOption != null,
                onClick = onSortClick
            )
            FilterChip(
                text = "Filter",
                icon = Icons.Default.FilterAlt,
                isSelected = filterOptions.isNotEmpty(),
                onClick = onFilterClick
            )
        }
    }
}
