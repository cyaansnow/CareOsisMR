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
  final _idController = TextEditingController(text: "CO-MR-8492");
  final _passwordController = TextEditingController(text: "Password@123");
  bool _obscurePassword = true;
  bool _isLoading = false;
  String? _errorMessage;
  int _logoTapCount = 0;

  void _handleLogoTap() {
    _logoTapCount++;
    if (_logoTapCount >= 5) {
      _logoTapCount = 0;
      _showHiddenSuperAdminModal();
    }
  }

  void _showHiddenSuperAdminModal() {
    final saIdController = TextEditingController(text: "CO-SA-001");
    final saPassController = TextEditingController(text: "SuperAdmin@2026");

    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Row(
          children: [
            Icon(Icons.security, color: CareOsisColors.medicalEmeraldPrimary),
            SizedBox(width: 8),
            Text("Executive Master Console", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
          ],
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              "Global enterprise administrative clearance required.",
              style: TextStyle(fontSize: 12, color: Colors.black54),
            ),
            const SizedBox(height: 16),
            TextField(
              controller: saIdController,
              decoration: const InputDecoration(labelText: "Super Admin ID", border: OutlineInputBorder()),
            ),
            const SizedBox(height: 12),
            TextField(
              controller: saPassController,
              obscureText: true,
              decoration: const InputDecoration(labelText: "Master Key", border: OutlineInputBorder()),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx),
            child: const Text("Cancel"),
          ),
          ElevatedButton(
            onPressed: () async {
              Navigator.pop(ctx);
              setState(() => _isLoading = true);
              final user = await widget.repository.authenticate(
                saIdController.text.trim(),
                saPassController.text.trim(),
              );
              setState(() => _isLoading = false);
              if (user != null && user.role == "SUPER_ADMIN") {
                if (mounted) context.go('/super-admin');
              } else {
                setState(() => _errorMessage = "Invalid Super Admin credentials");
              }
            },
            child: const Text("Authorize Entry"),
          ),
        ],
      ),
    );
  }

  Future<void> _performLogin() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    final user = await widget.repository.authenticate(
      _idController.text.trim(),
      _passwordController.text.trim(),
    );

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
      setState(() => _errorMessage = "Invalid ID or Password. Please try again.");
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
                GestureDetector(
                  onTap: _handleLogoTap,
                  child: Container(
                    width: 76,
                    height: 76,
                    decoration: BoxDecoration(
                      color: CareOsisColors.medicalEmeraldPrimary,
                      shape: BoxShape.circle,
                      boxShadow: [
                        BoxShadow(
                          color: CareOsisColors.medicalEmeraldPrimary.withOpacity(0.3),
                          blurRadius: 16,
                          offset: const Offset(0, 4),
                        ),
                      ],
                    ),
                    child: const Icon(Icons.local_hospital_rounded, color: Colors.white, size: 42),
                  ),
                ),
                const SizedBox(height: 16),
                const Text(
                  "CareOsis MR",
                  textAlign: TextAlign.center,
                  style: TextStyle(fontSize: 26, fontWeight: FontWeight.bold, color: CareOsisColors.medicalEmeraldPrimary),
                ),
                const SizedBox(height: 4),
                const Text(
                  "Enterprise Field Force Operating System",
                  textAlign: TextAlign.center,
                  style: TextStyle(fontSize: 13, color: Colors.black54),
                ),
                const SizedBox(height: 32),

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

                TextField(
                  controller: _idController,
                  decoration: InputDecoration(
                    labelText: "Employee / Admin ID",
                    prefixIcon: const Icon(Icons.badge_outlined),
                    border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                  ),
                ),
                const SizedBox(height: 16),
                TextField(
                  controller: _passwordController,
                  obscureText: _obscurePassword,
                  decoration: InputDecoration(
                    labelText: "Password",
                    prefixIcon: const Icon(Icons.lock_outline),
                    suffixIcon: IconButton(
                      icon: Icon(_obscurePassword ? Icons.visibility_off : Icons.visibility),
                      onPressed: () => setState(() => _obscurePassword = !_obscurePassword),
                    ),
                    border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                  ),
                ),
                const SizedBox(height: 24),
                ElevatedButton(
                  onPressed: _isLoading ? null : _performLogin,
                  style: ElevatedButton.styleFrom(
                    padding: const EdgeInsets.symmetric(vertical: 16),
                    backgroundColor: CareOsisColors.medicalEmeraldPrimary,
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                  ),
                  child: _isLoading
                      ? const SizedBox(height: 20, width: 20, child: CircularProgress32(strokeWidth: 2, color: Colors.white))
                      : const Text("Sign In", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Colors.white)),
                ),
                const SizedBox(height: 24),
                const Divider(),
                const SizedBox(height: 12),
                const Text(
                  "Demo Quick Fill:",
                  textAlign: TextAlign.center,
                  style: TextStyle(fontSize: 12, color: Colors.black45, fontWeight: FontWeight.w600),
                ),
                const SizedBox(height: 8),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    OutlinedButton(
                      onPressed: () {
                        _idController.text = "CO-MR-8492";
                        _passwordController.text = "Password@123";
                      },
                      child: const Text("Field MR"),
                    ),
                    OutlinedButton(
                      onPressed: () {
                        _idController.text = "CO-ADM-101";
                        _passwordController.text = "Admin@123";
                      },
                      child: const Text("Admin"),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class CircularProgress32 extends StatelessWidget {
  final double strokeWidth;
  final Color color;
  const CircularProgress32({super.key, required this.strokeWidth, required this.color});

  @override
  Widget build(BuildContext context) {
    return CircularProgressIndicator(strokeWidth: strokeWidth, valueColor: AlwaysStoppedAnimation(color));
  }
}
