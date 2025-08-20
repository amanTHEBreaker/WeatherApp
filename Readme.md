# ☀️ Weather Forecast App

An elegant **Weather Forecast App** built with **Jetpack Compose** following **Google’s recommended MVVM architecture**.
Minimal, scalable, and modern — designed with best practices in mind. 🚀

---

## ✨ Features


- [x] 🎨 **Jetpack Compose UI** – Beautiful, declarative UI  
- [x] 🧭 **Navigation Component** – Seamless screen transitions  
- [x] 🗂️ **MVVM + StateFlow** – Clean architecture with unidirectional data flow  
- [x] 🌐 **Weather API Integration**
- [x] ⚡ **Kotlin Coroutines** – Asynchronous, responsive experience  
- [ ] 📐 **Responsive UI** – Adapts to different screen sizes  
- [ ] 🌙 **Dark/Light Theme Support** – (Planned)  
- [ ]    **Add Menu System for lots of option like Themes, Adding the cities to it such that it would show dynamically in the home page.**
- [ ]    **Adding nice loading feature**


---

## 📸 Screenshots

| Welcome Screen                                   	| Forecast Screen                                   |
| ------------------------------------------------ 	| ------------------------------------------------- |
| <img src="screenshots/screen1.jpeg" width="250"/> | <img src="screenshots/screen2.jpeg" width="250"/> |
| Welcome Screen                                   	| 	                                  				|
| ------------------------------------------------ 	| -------------------------------------------------  |
| <img src="screenshots/screen3.jpeg" width="250"/> |  													|
---

## 🏗️ Architecture

This project strictly follows **Google’s official architecture guidance** for Android apps:

```
Presentation (UI) → ViewModel → Repository → Data Source
```

* **UI Layer** → Jetpack Compose + Material3
* **ViewModel Layer** → `StateFlow` for reactive state management
* **Data Layer** → Repository pattern to handle API/Local data
* **Dependency Injection** → Hilt (if added later)

---

## 🛠️ Tech Stack

* [Kotlin](https://kotlinlang.org/) – 100% Kotlin
* [Jetpack Compose](https://developer.android.com/jetpack/compose) – Modern UI toolkit
* [Material3](https://m3.material.io/) – Clean design system
* [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) + [StateFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)
* [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) – Async programming
* [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
* (Optional) [Retrofit](https://square.github.io/retrofit/) / [Ktor](https://ktor.io/) – For weather API calls

---

## 🚀 Getting Started

### 1️⃣ Clone the repo

```bash
git clone https://github.com/amanTHEBreaker/WeatherApp.git
cd WeatherApp
```

### 2️⃣ Open in Android Studio

* Use **Android Studio Ladybug (or latest)**
* Ensure **Kotlin + Compose** plugins are up-to-date

### 3️⃣ Run the app

* Connect a device/emulator
* Hit **Run ▶️**

---

## 📂 Project Structure

```
weather-forecast-app/
│── app/
│   ├── data/        # Data sources
│   ├── ui/          # Compose UI screens
│   ├── viewmodel/   # ViewModels with StateFlow
│   └── utils/       # Helpers/extensions
```

---

## 🤝 Contributing

Contributions are welcome! 🎉

* Fork the repo
* Create a feature branch
* Submit a PR 🚀

---

## ⭐ Support

If you find this project helpful, consider **starring ⭐ the repo** — it helps more people discover it!

---

