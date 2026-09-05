import 'package:flutter/material.dart';
import 'app.dart';
import 'data/local/db/careosis_database.dart';
import 'data/repository/careosis_repository.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  final database = CareOsisDatabase.instance;
  final repository = CareOsisRepository(database);

  runApp(CareOsisApp(repository: repository));
}
