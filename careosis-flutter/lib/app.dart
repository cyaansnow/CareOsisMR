import 'package:flutter/material.dart';
import 'core/theme/careosis_theme.dart';
import 'core/routing/app_router.dart';
import 'data/repository/careosis_repository.dart';

class CareOsisApp extends StatefulWidget {
  final CareOsisRepository repository;
  const CareOsisApp({super.key, required this.repository});

  @override
  State<CareOsisApp> createState() => _CareOsisAppState();
}

class _CareOsisAppState extends State<CareOsisApp> {
  late final _router = createRouter(widget.repository);

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      title: "CareOsis MR",
      debugShowCheckedModeBanner: false,
      theme: CareOsisTheme.lightTheme,
      routerConfig: _router,
    );
  }
}
