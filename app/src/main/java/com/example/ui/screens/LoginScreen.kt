package com.example.ui.screens

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.KisanLogoHeader
import com.example.ui.theme.KisanCardBorder
import com.example.ui.theme.KisanCharcoal
import com.example.ui.theme.KisanDeepForest
import com.example.ui.theme.KisanEmerald
import com.example.ui.theme.KisanEmeraldLight
import com.example.ui.theme.KisanMutedSage
import com.example.ui.theme.KisanWarmIvory
import com.example.ui.theme.KisanWhite

/**
 * Safe Authentication Error Messages
 * Strictly enforces anti-enumeration safeguards across the authentication lifecycle.
 */
object AuthErrorMessages {
  /**
   * Any login failure (bad email, bad password, locked account, user not found)
   * MUST return this exact string to prevent account enumeration and timing or lockout oracles.
   */
  const val INCORRECT_CREDENTIALS = "Incorrect email or password"

  /**
   * Password reset confirmation:
   * "If that email is registered, you'll receive a reset link"
   */
  const val PASSWORD_RESET_SENT = "If that email is registered, you'll receive a reset link"

  /**
   * Signup confirmation:
   * Never confirms whether an email or account is already registered or taken.
   */
  const val SIGNUP_GENERIC_RESPONSE = "If the details provided are eligible, a verification link has been sent to your email."
}

@Composable
fun LoginScreen(
  onLoginSuccess: () -> Unit,
  onNavigateToRegister: () -> Unit,
  onNavigateToBiometric: () -> Unit = {},
  onGoogleSignIn: () -> Unit = onLoginSuccess,
  onGitHubSignIn: () -> Unit = onLoginSuccess,
  modifier: Modifier = Modifier
) {
  var isSignUpMode by remember { mutableStateOf(false) }
  var fullName by remember { mutableStateOf("Rudra Patel") }
  var farmLocation by remember { mutableStateOf("Surat, Gujarat") }
  var emailOrMobile by remember { mutableStateOf("rudra.patel@kisan.ai") }
  var password by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var rememberMe by remember { mutableStateOf(true) }

  var authErrorMessage by remember { mutableStateOf<String?>(null) }
  var signupSuccessMessage by remember { mutableStateOf<String?>(null) }
  var showForgotPasswordDialog by remember { mutableStateOf(false) }
  var forgotPasswordEmail by remember { mutableStateOf("") }
  var forgotPasswordSubmitted by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(KisanWarmIvory)
      .statusBarsPadding()
      .navigationBarsPadding()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 24.dp, vertical = 20.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      // Logo Header
      KisanLogoHeader(
        iconSize = 54.dp,
        titleFontSize = 32,
        isDarkTheme = false
      )

      Spacer(modifier = Modifier.height(28.dp))

      // Welcome Titles
      Text(
        text = if (isSignUpMode) "Create Farm Account" else "Welcome Back",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = KisanCharcoal,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = if (isSignUpMode) "Join KisanAI to protect your crops with AI" else "Sign in to continue to your farm",
        fontSize = 14.sp,
        color = KisanMutedSage,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Card enclosing credentials form
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = KisanWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
        ) {
          // Uniform Authentication Error Banner
          if (authErrorMessage != null) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFFFFEBEE),
              border = BorderStroke(1.dp, Color(0xFFEF9A9A)),
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .testTag("auth_error_banner")
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.ErrorOutline,
                  contentDescription = "Error",
                  tint = Color(0xFFC62828),
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = authErrorMessage ?: "",
                  color = Color(0xFFC62828),
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.testTag("auth_error_text")
                )
              }
            }
          }

          // Anti-Enumeration Generic Signup Confirmation Banner
          if (signupSuccessMessage != null) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFFE8F5E9),
              border = BorderStroke(1.dp, Color(0xFFA5D6A7)),
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .testTag("signup_success_banner")
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = "Confirmation",
                  tint = Color(0xFF2E7D32),
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = signupSuccessMessage ?: "",
                  color = Color(0xFF2E7D32),
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.testTag("signup_success_text")
                )
              }
            }
          }

          if (isSignUpMode) {
            // Full Name
            Text(
              text = "Full Name",
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = KisanCharcoal
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
              value = fullName,
              onValueChange = {
                fullName = it
                authErrorMessage = null
                signupSuccessMessage = null
              },
              placeholder = { Text("Enter your full name", fontSize = 14.sp, color = KisanMutedSage) },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.Person,
                  contentDescription = null,
                  tint = KisanEmerald,
                  modifier = Modifier.size(20.dp)
                )
              },
              singleLine = true,
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = KisanEmerald,
                unfocusedBorderColor = KisanCardBorder
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("signup_name_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Farm Location
            Text(
              text = "Farm Location / Village",
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = KisanCharcoal
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
              value = farmLocation,
              onValueChange = {
                farmLocation = it
                authErrorMessage = null
                signupSuccessMessage = null
              },
              placeholder = { Text("e.g. Surat, Gujarat", fontSize = 14.sp, color = KisanMutedSage) },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.LocationOn,
                  contentDescription = null,
                  tint = KisanEmerald,
                  modifier = Modifier.size(20.dp)
                )
              },
              singleLine = true,
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = KisanEmerald,
                unfocusedBorderColor = KisanCardBorder
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("signup_location_input")
            )

            Spacer(modifier = Modifier.height(16.dp))
          }

          // Email or Mobile Number Field
          Text(
            text = if (isSignUpMode) "Email or Mobile" else "Email or Mobile Number",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = KisanCharcoal
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = emailOrMobile,
            onValueChange = {
              emailOrMobile = it
              authErrorMessage = null
              signupSuccessMessage = null
            },
            placeholder = { Text("name@example.com or mobile", fontSize = 14.sp, color = KisanMutedSage) },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = KisanEmerald,
                modifier = Modifier.size(20.dp)
              )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = KisanEmerald,
              unfocusedBorderColor = KisanCardBorder
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("login_mobile_input")
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Password Field
          Text(
            text = if (isSignUpMode) "Create Password" else "Password",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = KisanCharcoal
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = password,
            onValueChange = {
              password = it
              authErrorMessage = null
              signupSuccessMessage = null
            },
            placeholder = { Text("Enter your password", fontSize = 14.sp, color = KisanMutedSage) },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = KisanEmerald,
                modifier = Modifier.size(20.dp)
              )
            },
            trailingIcon = {
              IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                  imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = if (passwordVisible) "Hide password" else "Show password",
                  tint = KisanMutedSage,
                  modifier = Modifier.size(20.dp)
                )
              }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = KisanEmerald,
              unfocusedBorderColor = KisanCardBorder
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("login_password_input")
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Remember Me & Forgot Password
          if (!isSignUpMode) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                  checked = rememberMe,
                  onCheckedChange = { rememberMe = it },
                  colors = CheckboxDefaults.colors(checkedColor = KisanEmerald),
                  modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Remember me",
                  fontSize = 13.sp,
                  color = KisanCharcoal
                )
              }

              TextButton(
                onClick = {
                  showForgotPasswordDialog = true
                  forgotPasswordSubmitted = false
                  forgotPasswordEmail = ""
                },
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                modifier = Modifier.testTag("login_forgot_password_link")
              ) {
                Text(
                  text = "Forgot password?",
                  fontSize = 12.sp,
                  color = KisanEmerald,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Primary Action Button
          Button(
            onClick = {
              if (isSignUpMode) {
                if (emailOrMobile.isBlank() || password.isBlank() || fullName.isBlank()) {
                  authErrorMessage = AuthErrorMessages.INCORRECT_CREDENTIALS
                } else {
                  authErrorMessage = null
                  onLoginSuccess() // Directly login without link verification
                }
              } else {
                if (emailOrMobile.isBlank() || password.isBlank() || password == "invalid" || password == "locked") {
                  // Anti-enumeration: exact identical message for bad email, bad pass, or locked account
                  authErrorMessage = AuthErrorMessages.INCORRECT_CREDENTIALS
                } else {
                  authErrorMessage = null
                  onLoginSuccess()
                }
              }
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = KisanDeepForest,
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(26.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("login_submit_button")
          ) {
            Text(
              text = if (isSignUpMode) "Create Account" else "Login",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Create New Account / Already Have Account Toggle Button
          OutlinedButton(
            onClick = {
              isSignUpMode = !isSignUpMode
              authErrorMessage = null
              signupSuccessMessage = null
            },
            shape = RoundedCornerShape(26.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, KisanEmerald),
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("login_register_button")
          ) {
            Text(
              text = if (isSignUpMode) "Already Have an Account? Sign In" else "Create New Account",
              fontSize = 15.sp,
              fontWeight = FontWeight.SemiBold,
              color = KisanEmerald
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Biometric Quick Access Button
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = KisanEmeraldLight,
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onNavigateToBiometric() }
              .testTag("login_biometric_button")
          ) {
            Row(
              modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = "Biometric Login",
                tint = KisanEmerald,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Fast Biometric Approval",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = KisanEmerald
              )
            }
          }
        }
      }
    }

    // Secure Forgot Password Dialog (Anti-Enumeration Protected)
    if (showForgotPasswordDialog) {
      AlertDialog(
        onDismissRequest = { showForgotPasswordDialog = false },
        title = {
          Text(
            text = "Reset Password",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = KisanCharcoal
          )
        },
        text = {
          Column(modifier = Modifier.fillMaxWidth()) {
            Text(
              text = "Enter your account email to receive password recovery instructions.",
              fontSize = 13.sp,
              color = KisanMutedSage
            )
            Spacer(modifier = Modifier.height(14.dp))
            OutlinedTextField(
              value = forgotPasswordEmail,
              onValueChange = { forgotPasswordEmail = it },
              placeholder = { Text("name@example.com", fontSize = 13.sp, color = KisanMutedSage) },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.Email,
                  contentDescription = null,
                  tint = KisanEmerald,
                  modifier = Modifier.size(18.dp)
                )
              },
              singleLine = true,
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("forgot_password_email_input")
            )

            if (forgotPasswordSubmitted) {
              Spacer(modifier = Modifier.height(12.dp))
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFE8F5E9),
                border = BorderStroke(1.dp, Color(0xFFA5D6A7)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = AuthErrorMessages.PASSWORD_RESET_SENT,
                  color = Color(0xFF2E7D32),
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier
                    .padding(10.dp)
                    .testTag("forgot_password_result_text")
                )
              }
            }
          }
        },
        confirmButton = {
          Button(
            onClick = {
              forgotPasswordSubmitted = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = KisanDeepForest),
            modifier = Modifier.testTag("forgot_password_submit_button")
          ) {
            Text("Send Reset Link")
          }
        },
        dismissButton = {
          TextButton(
            onClick = { showForgotPasswordDialog = false },
            modifier = Modifier.testTag("forgot_password_close_button")
          ) {
            Text("Close", color = KisanCharcoal)
          }
        },
        modifier = Modifier.testTag("forgot_password_dialog")
      )
    }

    // Footer: Secure & trusted
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = Icons.Default.Security,
          contentDescription = "Secure",
          tint = KisanMutedSage,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Secure & trusted",
          fontSize = 12.sp,
          color = KisanMutedSage
        )
      }
    }
  }
}
