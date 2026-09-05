import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:geolocator/geolocator.dart';

class LocationTrackingService {
  static LocationTrackingService? _instance;
  static LocationTrackingService get instance => _instance ??= LocationTrackingService._();
  LocationTrackingService._();

  bool _isTracking = false;
  bool get isTracking => _isTracking;

  Position? _lastPosition;
  Position? get lastPosition => _lastPosition;

  StreamSubscription<Position>? _positionSubscription;
  final _trackingStateController = StreamController<bool>.broadcast();
  final _positionController = StreamController<Position?>.broadcast();

  Stream<bool> get trackingStateStream async* {
    yield _isTracking;
    yield* _trackingStateController.stream;
  }

  Stream<Position?> get positionStream async* {
    yield _lastPosition;
    yield* _positionController.stream;
  }

  /// Request permissions and verify location services are enabled
  Future<bool> checkAndRequestPermission() async {
    try {
      final serviceEnabled = await Geolocator.isLocationServiceEnabled();
      if (!serviceEnabled) {
        debugPrint("[GPS] Location service is disabled on device.");
        return false;
      }

      var permission = await Geolocator.checkPermission();
      if (permission == LocationPermission.denied) {
        permission = await Geolocator.requestPermission();
        if (permission == LocationPermission.denied) {
          debugPrint("[GPS] Location permission was denied by user.");
          return false;
        }
      }

      if (permission == LocationPermission.deniedForever) {
        debugPrint("[GPS] Location permission is permanently denied.");
        return false;
      }

      return true;
    } catch (e) {
      debugPrint("[GPS] Error requesting location permissions: $e");
      return false;
    }
  }

  /// Get a one-shot current high-accuracy position
  Future<Position?> getCurrentPosition() async {
    try {
      final hasPermission = await checkAndRequestPermission();
      if (!hasPermission) {
        return _lastPosition;
      }

      final position = await Geolocator.getCurrentPosition(
        desiredAccuracy: LocationAccuracy.high,
        timeLimit: const Duration(seconds: 10),
      );

      _lastPosition = position;
      _positionController.add(position);
      return position;
    } catch (e) {
      debugPrint("[GPS] Exception acquiring current position: $e");
      return _lastPosition;
    }
  }

  /// Start continuous tracking upon Attendance Check-In
  Future<bool> startTracking({
    required String mrId,
    required String attendanceId,
    void Function(Position position)? onPositionUpdate,
  }) async {
    final hasPermission = await checkAndRequestPermission();
    if (!hasPermission) {
      debugPrint("[GPS] Cannot start tracking without location permissions.");
      return false;
    }

    _isTracking = true;
    _trackingStateController.add(true);

    // Initial fix
    final initialPos = await getCurrentPosition();
    if (initialPos != null && onPositionUpdate != null) {
      onPositionUpdate(initialPos);
    }

    // Cancel any existing subscription
    await _positionSubscription?.cancel();

    // Configure location stream settings (update every 25m or on significant change)
    const locationSettings = LocationSettings(
      accuracy: LocationAccuracy.high,
      distanceFilter: 25, // meters
    );

    _positionSubscription = Geolocator.getPositionStream(
      locationSettings: locationSettings,
    ).listen(
      (Position position) {
        _lastPosition = position;
        _positionController.add(position);
        if (onPositionUpdate != null) {
          onPositionUpdate(position);
        }
        debugPrint("[GPS Tracker] Ping: ${position.latitude}, ${position.longitude} (Acc: ${position.accuracy}m)");
      },
      onError: (err) {
        debugPrint("[GPS Tracker] Stream error: $err");
      },
    );

    debugPrint("[GPS Tracker] Active field tracking started for MR $mrId (Attendance: $attendanceId)");
    return true;
  }

  /// Stop tracking upon Attendance Check-Out
  Future<void> stopTracking() async {
    await _positionSubscription?.cancel();
    _positionSubscription = null;
    _isTracking = false;
    _trackingStateController.add(false);
    debugPrint("[GPS Tracker] Field tracking stopped.");
  }
}
