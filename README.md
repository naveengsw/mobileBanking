# MobileBanking v1

This workspace now contains:
- A Java Spring Boot backend for authentication at [backend](backend)
- A native Android client in [android](android)

## Backend
Run the backend with:

```bash
cd backend
mvn spring-boot:run
```

The login endpoint is:
- POST http://localhost:8080/api/login

## Android app
Open the Android project in Android Studio from [android](android), then run the app on an emulator or device.

The app sends login requests to:
- http://10.0.2.2:8080/api/login

## Verified status
- Backend build: verified with `mvn -q -DskipTests package`
- Android build: verified with `./gradlew assembleDebug`
- APK output: [android/app/build/outputs/apk/debug/app-debug.apk](android/app/build/outputs/apk/debug/app-debug.apk)
