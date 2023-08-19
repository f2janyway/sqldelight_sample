@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.sql_test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.sql_test.Database.Companion.Schema
import com.example.sqltest.User

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         * [Schema] is generated code by sqldelight from
         * build.gradle(root)
         * ```
         * sqldelight {
         *     databases {
         *         create("Database") {
         *             packageName.set("com.example.sql_test")
         *         }
         *     }
         * }
         * ```
         * so if not reference Database.Schema
         * (from doc; but this Database.Companion.Schema; maybe it from v2.0.0)
         * after set all of build.gradle(s) you must `build` for generating code
         */
        val driver: SqlDriver = AndroidSqliteDriver(
            schema = Schema,
            context = applicationContext,
            name = "user.db"
        )
        // this code just for showing usage of sqldelight easily,
        // it's better to extract(split) db code
        val db = Database(driver)
        val userQueries = db.userQueries

        setContent {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                val allUserState by userQueries.selectAll().asFlow().collectAsState(initial = null)
                val allUser by remember(allUserState) {
                    derivedStateOf { allUserState?.executeAsList() }
                }
                PutUserTextField(onClickInsert = {
                    userQueries.insert(null, it)
                })
                AllUserList(users = allUser, onClickDelete = userQueries::deleteById)
            }
        }
    }
}

@Composable
private fun PutUserTextField(onClickInsert: (String) -> Unit, modifier: Modifier = Modifier) {
    val text = remember {
        mutableStateOf("")
    }
    TextField(value = text.value, onValueChange = { text.value = it },
        placeholder = {
            Text("Put user full name")
        }, trailingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { text.value = "" }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        tint = Color.Red
                    )
                }
                IconButton(onClick = {
                    onClickInsert.invoke(text.value)
                    text.value = ""
                }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                }

            }
        }, modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun AllUserList(users: List<User>?,onClickDelete: (Long) -> Unit, modifier: Modifier = Modifier) {
    users ?: run {
        Text(text = "none user", modifier = modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        return
    }

    LazyColumn(modifier = modifier) {
        item {
            Text(text = "user count: ${users.count()}", modifier = Modifier.padding(16.dp))
        }
        itemsIndexed(users) { idx,user ->
            UserItem(index = idx,user = user,onClickDelete)
        }
    }
}

@Composable
fun UserItem(index:Int,user: User, onClickDelete: (Long) -> Unit, modifier: Modifier = Modifier) {
    Row {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)) {
                    append((index + 1).toString())
                    append("\t\t")
                }
                withStyle(SpanStyle(fontSize = 14.sp)) {
                    append(user.full_name)
                }
            }, modifier = modifier
                .weight(1f)
                .padding(16.dp)
        )
        IconButton(onClick = { onClickDelete.invoke(user.id) }) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = null)
        }
    }

}
