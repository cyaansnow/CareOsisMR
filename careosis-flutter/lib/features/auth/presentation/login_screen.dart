import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../core/theme/careosis_theme.dart';
import '../../../data/repository/careosis_repository.dart';

class LoginScreen extends StatefulWidget {
  final CareOsisRepository repository;
  const LoginScreen({super.key, required this.repository});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  bool _isRegisterMode = false;

  // Controllers - Initialized completely empty (Zero Mock Defaults)
  final _idController = TextEditingController();
  final _passwordController = TextEditingController();
  final _nameController = TextEditingController();
  final _territoryController = TextEditingController();
  final _phoneController = TextEditingController();

  bool _obscurePassword = true;
  bool _isLoading = false;
  String? _errorMessage;

  @override
  void dispose() {
    _idController.dispose();
    _passwordController.dispose();
    _nameController.dispose();
    _territoryController.dispose();
    _phoneController.dispose();
    super.dispose();
  }

  Future<void> _performAuth() async {
    final identifier = _idController.text.trim();
    final password = _passwordController.text.trim();

    if (identifier.isEmpty || password.isEmpty) {
      setState(() => _errorMessage = "Please enter your Email/ID and Password.");
      return;
    }

    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      if (_isRegisterMode) {
        final name = _nameController.text.trim();
        final territory = _territoryController.text.trim();
        final phone = _phoneController.text.trim();

        if (name.isEmpty) {
          setState(() {
            _isLoading = false;
            _errorMessage = "Please enter your full name.";
          });
          return;
        }

        final newUser = await widget.repository.register(
          email: identifier,
          password: password,
          fullName: name,
          hqTerritory: territory.isNotEmpty ? territory : "Field Territory",
          phone: phone,
        );

        setState(() => _isLoading = false);

        if (newUser != null) {
          if (!mounted) return;
          context.go('/home');
        } else {
          setState(() => _errorMessage = "Registration failed. Please verify your details.");
        }
      } else {
        final user = await widget.repository.authenticate(identifier, password);
        setState(() => _isLoading = false);

        if (user != null) {
          if (!mounted) return;
          if (user.role == "SUPER_ADMIN") {
            context.go('/super-admin');
          } else if (user.role == "ADMIN") {
            context.go('/admin/dashboard');
          } else {
            context.go('/home');
          }
        } else {
          setState(() => _errorMessage = "Invalid credentials. Please verify ID/Email and password.");
        }
      }
    } catch (e) {
      setState(() {
        _isLoading = false;
        _errorMessage = "Authentication error: $e";
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.symmetric(horizontal: 28, vertical: 24),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                // Brand Header
                Container(
                  width: 72,
                  height: 72,
                  margin: const EdgeInsets.only(bottom: 16),
                  decoration: BoxDecoration(
                    color: CareOsisColors.medicalEmeraldPrimary,
                    shape: BoxShape.circle,
                    boxShadow: [
                      BoxShadow(
                        color: CareOsisColors.medicalEmeraldPrimary.withOpacity(0.25),
                        blurRadius: 16,
                        offset: const Offset(0, 4),
                      ),
                    ],
                  ),
                  child: const Icon(Icons.local_hospital_rounded, color: Colors.white, size: 40),
                ),
                const Text(
                  "CareOsis MR",
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    fontSize: 26,
                    fontWeight: FontWeight.bold,
                    color: CareOsisColors.medicalEmeraldPrimary,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  _isRegisterMode
                      ? "Create Medical Representative Account"
                      : "Enterprise Field Force Authentication",
                  textAlign: TextAlign.center,
                  style: const TextStyle(fontSize: 13, color: Colors.black54),
                ),
                const SizedBox(height: 28),

                // Tab Selector (Sign In vs Register)
                Container(
                  decoration: BoxDecoration(
                    color: const Color(0xFFF1F5F9),
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Row(
                    children: [
                      Expanded(
                        child: GestureDetector(
                          onTap: () => setState(() {
                            _isRegisterMode = false;
                            _errorMessage = null;
                          }),
                          child: Container(
                            padding: const EdgeInsets.symmetric(vertical: 10),
                            decoration: BoxDecoration(
                              color: !_isRegisterMode ? CareOsisColors.medicalEmeraldPrimary : Colors.transparent,
                              borderRadius: BorderRadius.circular(10),
                            ),
                            child: Text(
                              "Sign In",
                              textAlign: TextAlign.center,
                              style: TextStyle(
                                fontWeight: FontWeight.bold,
                                fontSize: 13,
                                color: !_isRegisterMode ? Colors.white : Colors.black54,
                              ),
                            ),
                          ),
                        ),
                      ),
                      Expanded(
                        child: GestureDetector(
                          onTap: () => setState(() {
                            _isRegisterMode = true;
                            _errorMessage = null;
                          }),
                          child: Container(
                            padding: const EdgeInsets.symmetric(vertical: 10),
                            decoration: BoxDecoration(
                              color: _isRegisterMode ? CareOsisColors.medicalEmeraldPrimary : Colors.transparent,
                              borderRadius: BorderRadius.circular(10),
                            ),
                            child: Text(
                              "Register",
                              textAlign: TextAlign.center,
                              style: TextStyle(
                                fontWeight: FontWeight.bold,
                                fontSize: 13,
                                color: _isRegisterMode ? Colors.white : Colors.black54,
                              ),
                            ),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 20),

                // Error Banner
                if (_errorMessage != null)
                  Container(
                    margin: const EdgeInsets.only(bottom: 16),
                    padding: const EdgeInsets.all(12),
                    decoration: BoxDecoration(
                      color: const Color(0xFFFFEBEE),
                      borderRadius: BorderRadius.circular(8),
                      border: Border.all(color: Colors.red.shade300),
                    ),
                    child: Row(
                      children: [
                        const Icon(Icons.error_outline, color: Colors.red, size: 20),
                        const SizedBox(width: 8),
                        Expanded(
                          child: Text(_errorMessage!, style: const TextStyle(color: Colors.red, fontSize: 13)),
                        ),
                      ],
                    ),
                  ),

                // Registration specific fields
                if (_isRegisterMode) ...[
                  TextField(
                    controller: _nameController,
                    decoration: InputDecoration(
                      labelText: "Full Name",
                      hintText: "e.g. Rahul Sharma",
                      prefixIcon: const Icon(Icons.person_outline),
                      border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                    ),
                  ),
                  const SizedBox(height: 14),
                  TextField(
                    controller: _territoryController,
                    decoration: InputDecoration(
                      labelText: "HQ / Territory",
                      hintText: "e.g. South Delhi & Saket",
                      prefixIcon: const Icon(Icons.location_city_outlined),
                      border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                    ),
                  ),
                  const SizedBox(height: 14),
                  TextField(
                    controller: _phoneController,
                    keyboardType: TextInputType.phone,
                    decoration: InputDecoration(
                      labelText: "Mobile Phone (Optional)",
                      hintText: "+91 9876543210",
                      prefixIcon: const Icon(Icons.phone_outlined),
                      border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                    ),
                  ),
                  const SizedBox(height: 14),
                ],

                // Common fields: Identifier & Password
                TextField(
                  controller: _idController,
                  keyboardType: _isRegisterMode ? TextInputType.emailAddress : TextInputType.text,
                  decoration: InputDecoration(
                    labelText: _isRegisterMode ? "Email Address" : "Employee ID or Email",
                    hintText: _isRegisterMode ? "you@pharma.com" : "Enter your ID or registered email",
                    prefixIcon: const Icon(Icons.badge_outlined),
                    border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                  ),
                ),
                const SizedBox(height: 14),
                TextField(
                  controller: _passwordController,
                  obscureText: _obscurePassword,
                  decoration: InputDecoration(
                    labelText: "Password",
                    hintText: "Enter secure password",
                    prefixIcon: const Icon(Icons.lock_outline),
                    suffixIcon: IconButton(
                      icon: Icon(_obscurePassword ? Icons.visibility_off : Icons.visibility),
                      onPressed: () => setState(() => _obscurePassword = !_obscurePassword),
                    ),
                    border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                  ),
                ),
                const SizedBox(height: 24),

                // Submit Button
                ElevatedButton(
                  onPressed: _isLoading ? null : _performAuth,
                  style: ElevatedButton.styleFrom(
                    padding: const EdgeInsets.symmetric(vertical: 16),
                    backgroundColor: CareOsisColors.medicalEmeraldPrimary,
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                  ),
                  child: _isLoading
                      ? const SizedBox(
                          height: 20,
                          width: 20,
                          child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white),
                        )
                      : Text(
                          _isRegisterMode ? "Create Account & Sign In" : "Sign In",
                          style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Colors.white),
                        ),
                ),
                const SizedBox(height: 16),

                // Live Cloud Indicator
                const Center(
                  child: Text(
                    "Connected to Supabase PostgreSQL & Offline Storage",
                    style: TextStyle(fontSize: 11, color: Colors.black38),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
