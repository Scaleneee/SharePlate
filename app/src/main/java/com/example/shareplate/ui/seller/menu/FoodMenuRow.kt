package com.example.shareplate.ui.seller.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.shareplate.R
import com.example.shareplate.data.FoodItems
import com.example.shareplate.data.model.FoodItem
import com.example.shareplate.ui.theme.SharePlateTheme

@Preview
@Composable
fun FoodMenuRowPreview() {
    SharePlateTheme {
        FoodMenuRow(foodItem = FoodItems.foodItems[0], onEditClick = {})
    }
}

@Composable
fun FoodMenuRow(
    foodItem: FoodItem,
    onEditClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally)
        ) {
            // food image
            if (foodItem.imageUri != null) {
                // if got image
                AsyncImage(
                    model = foodItem.imageUri,
                    contentDescription = foodItem.foodName,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // if no image, show default icon
                Image(
                    painter = painterResource(R.drawable.food),
                    contentDescription = "No food image",
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            // food name, original price, best before in a column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // food name
                Text(
                    text = foodItem.foodName,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                // original price
                Text(
                    text = "Original Price: RM%.2f".format(
                        foodItem.originalPriceCent / 100.0
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                // best before days
                Text(
                    text = "Best Before   : ${foodItem.bestBeforeDays} days",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            // edit button
            TextButton(
                onClick = {onEditClick(foodItem.foodItemId)},
            ) {
                Text(text = "Edit")
            }
        }
    }
}