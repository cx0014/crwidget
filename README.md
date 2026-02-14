# Course Reminder Widget

![](https://img.shields.io/badge/Made%20with-Kotlin-purple?style=for-the-badge&logo=kotlin)

## About

This is an Android application that allows you to place a widget on your home screen. You can input your course data and the widget will display the next course to go to.

Note that this app does not account for holidays, exams, or other special events (as it does not use external university APIs), so don't trust the app completely.

This app has been created in Android Studio with Jetpack Compose and Jetpack Glance.

## Screenshots

| Main Application Screen                                         | Widget on a Home Screen                                       |
|-----------------------------------------------------------------|---------------------------------------------------------------|
| ![](/screenshots/CRWidget_Screenshot_MainActivity.png?raw=true) | ![](/screenshots/CRWidget_Screenshot_HomeScreen.png?raw=true) |

## Features

- All saved courses displayed neatly as unified list
- Automatically categorizes courses based on date and time
- Easily edit course configuration by clicking on main list items

## Possible Improvements

- Make widgets resizable with dynamic layouts
- Import courses from with UST API (if possible)