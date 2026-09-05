import 'package:flutter/material.dart';
import 'app.dart';
import 'core/services/supabase_sync_service.dart';
import 'data/local/db/careosis_database.dart';
import 'data/repository/careosis_repository.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await SupabaseSyncService.instance.initialize();
  final database = CareOsisDatabase.instance;
  final repository = CareOsisRepository(database);

  runApp(CareOsisApp(repository: repository));
}

