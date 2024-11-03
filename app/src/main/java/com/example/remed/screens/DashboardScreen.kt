package com.example.remed.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.remed.R
import com.example.remed.navigation.HomeRouteScreens

@Composable
fun DashboardScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical =  40.dp)
    ) {
        // Greeting message with avatar
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Welcome, Hansaja!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Icon(
                imageVector = Icons.Filled.VerifiedUser,
                contentDescription = "User Avatar",
                tint = Color.Blue,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Banner image
        Image(
            painter = painterResource(id = R.drawable.login),
            contentDescription = "Banner Image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .aspectRatio(9f / 16f) // Maintain aspect ratio
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search bar
//        BasicTextField(
//            value = TextFieldValue(""),
//            onValueChange = {},
//            decorationBox = { innerTextField ->
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(50.dp)
//                        .clip(RoundedCornerShape(12.dp))
//                        .background(Color.LightGray)
//                        .padding(horizontal = 16.dp, vertical = 12.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        imageVector = Icons.Filled.Search,
//                        contentDescription = "Search Icon",
//                        tint = Color.Gray
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    innerTextField()
//                }
//            }
//        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category selection boxes
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CategoryBox(title = "Search using medicines") {
                navController.navigate(HomeRouteScreens.SearchMedicine.route)
            }
            CategoryBox(title = "Search Nearby pharmacies") {
                navController.navigate(HomeRouteScreens.SearchPharmacy.route)
            }
        }
    }
}

@Composable
fun CategoryBox(title: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(150.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.LightGray)
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    // Pass a dummy NavController for preview purposes
    // Replace with actual NavController in your app
    // DashboardScreen(navController = rememberNavController())
}
