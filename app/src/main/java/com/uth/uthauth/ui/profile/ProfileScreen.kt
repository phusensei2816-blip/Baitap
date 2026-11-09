package com.uth.uthauth.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uth.uthauth.R
import com.uth.uthauth.domain.model.AuthUser
import com.uth.uthauth.ui.AppViewModelProvider
import com.uth.uthauth.ui.components.InitialsAvatar
import com.uth.uthauth.ui.components.LanguageToggle
import com.uth.uthauth.ui.profile.components.ProfileActions
import com.uth.uthauth.ui.profile.components.StatCard
import com.uth.uthauth.ui.profile.components.WeeklyChart
import com.uth.uthauth.ui.theme.DangerRed
import com.uth.uthauth.ui.theme.SuccessGreen
import com.uth.uthauth.ui.theme.SuccessSoft
import com.uth.uthauth.util.shareInvite

@Composable
fun ProfileRoute(
    onBack: () -> Unit,
    onSignedOut: () -> Unit,
    viewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showConfirm by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val testErrorToast = stringResource(R.string.toast_test_error)

    ProfileScreen(
        state = state,
        onBack = onBack,
        onSignOutClick = { showConfirm = true },
        onInviteClick = { shareInvite(context) },
        onTestErrorClick = {
            viewModel.sendTestError()
            Toast.makeText(context, testErrorToast, Toast.LENGTH_SHORT).show()
        }
    )

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text(stringResource(R.string.signout_title)) },
            text = {
                Text(
                    stringResource(
                        if (state.user?.isAnonymous == true) R.string.signout_guest_msg
                        else R.string.signout_msg
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showConfirm = false
                    viewModel.signOut(onDone = onSignedOut)
                }) { Text(stringResource(R.string.signout), color = DangerRed) }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onBack: () -> Unit,
    onSignOutClick: () -> Unit,
    onInviteClick: () -> Unit,
    onTestErrorClick: () -> Unit,
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 20.dp)
        ) {
            ProfileTopBar(onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                state.user?.let { UserInfo(it) }

                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard("${state.summary.done}", stringResource(R.string.stat_done), Modifier.weight(1f))
                    StatCard("${state.summary.pending}", stringResource(R.string.stat_pending), Modifier.weight(1f))
                    StatCard("${state.summary.percent}%", stringResource(R.string.stat_percent), Modifier.weight(1f))
                }

                Spacer(Modifier.height(14.dp))
                WeeklyChart(state.weekly, Modifier.fillMaxWidth())
                Spacer(Modifier.height(14.dp))
                ProfileActions(onInviteClick, onTestErrorClick)
                Spacer(Modifier.height(16.dp))
            }

            SignOutButton(onClick = onSignOutClick, modifier = Modifier.padding(vertical = 16.dp))
        }
    }
}

@Composable
private fun ProfileTopBar(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onBack)
                .padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back_desc),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.size(4.dp))
            Text(
                stringResource(R.string.profile_back),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )
        }
        Text(
            stringResource(R.string.profile_title),
            modifier = Modifier.align(Alignment.Center),
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )
        LanguageToggle(modifier = Modifier.align(Alignment.CenterEnd))
    }
}

@Composable
private fun UserInfo(user: AuthUser) {
    val guestInitial = stringResource(R.string.guest_initial)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        InitialsAvatar(user.initials(guestInitial), size = 76.dp, fontSize = 26.sp)
        Spacer(Modifier.height(12.dp))
        Text(
            user.nameOrNull ?: stringResource(R.string.guest_name),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = user.email ?: stringResource(R.string.no_email),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        LinkBadge(linked = user.isGoogleLinked)
    }
}

@Composable
private fun LinkBadge(linked: Boolean) {
    val bg = if (linked) SuccessSoft else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    val fg = if (linked) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (linked) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = fg, modifier = Modifier.size(12.dp))
            Spacer(Modifier.size(4.dp))
        }
        Text(
            stringResource(if (linked) R.string.badge_google else R.string.badge_guest),
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SignOutButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.AutoMirrored.Outlined.Logout,
            contentDescription = null,
            tint = DangerRed,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.size(8.dp))
        Text(
            stringResource(R.string.signout),
            color = DangerRed,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}
