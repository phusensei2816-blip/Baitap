package com.example.emailchange.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.emailchange.GiaoDienUI.ConfirmScreen
import com.example.emailchange.GiaoDienUI.ForgotPasswordScreen
import com.example.emailchange.GiaoDienUI.ResetPasswordScreen
import com.example.emailchange.GiaoDienUI.VerifyCodeScreen
import com.example.emailchange.model.ResetInfo

@Composable
fun ResetNavGraph() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = ResetRoute.Forgot()) {

        // Màn 1: nhận dữ liệu (nếu có) từ màn 4, gửi email sang màn 2
        composable<ResetRoute.Forgot> { entry ->
            val r = entry.toRoute<ResetRoute.Forgot>()
            ForgotPasswordScreen(
                received = ResetInfo(r.email, r.otp, r.password),
                onNext = { email -> nav.navigate(ResetRoute.Verify(email)) }
            )
        }

        // Màn 2: nhận email, gửi thêm otp sang màn 3
        composable<ResetRoute.Verify> { entry ->
            val r = entry.toRoute<ResetRoute.Verify>()
            VerifyCodeScreen(
                email = r.email,
                onBack = { nav.popBackStack() },
                onNext = { otp -> nav.navigate(ResetRoute.NewPassword(r.email, otp)) }
            )
        }

        // Màn 3: nhận email + otp, gửi thêm password sang màn 4
        composable<ResetRoute.NewPassword> { entry ->
            val r = entry.toRoute<ResetRoute.NewPassword>()
            ResetPasswordScreen(
                onBack = { nav.popBackStack() },
                onNext = { pass -> nav.navigate(ResetRoute.Confirm(r.email, r.otp, pass)) }
            )
        }

        // Màn 4: nhận đủ 3 dữ liệu, Submit thì gửi ngược về màn 1
        composable<ResetRoute.Confirm> { entry ->
            val r = entry.toRoute<ResetRoute.Confirm>()
            val info = ResetInfo(r.email, r.otp, r.password)
            ConfirmScreen(
                info = info,
                onBack = { nav.popBackStack() },
                onSubmit = {
                    nav.navigate(ResetRoute.Forgot(info.email, info.otp, info.password)) {
                        popUpTo<ResetRoute.Forgot> { inclusive = true }
                    }
                }
            )
        }
    }
}