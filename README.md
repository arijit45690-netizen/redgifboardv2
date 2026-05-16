# 🔞 RedGif Board

> A custom Android GIF keyboard powered by [RedGifs](https://www.redgifs.com) — search, browse, and send GIFs directly into any chat app.

![Platform](https://img.shields.io/badge/Platform-Android%208.0%2B-brightgreen)
![Language](https://img.shields.io/badge/Language-Kotlin-purple)
![Build](https://img.shields.io/badge/Build-GitHub%20Actions-blue)
![Version](https://img.shields.io/badge/Version-2.0-red)

---

## 📱 What It Does

RedGif Board replaces your Android keyboard with a GIF search panel. Open any chat app, switch to RedGif Board, and you can:

- 🔍 **Search** any GIF using a built-in QWERTY keyboard
- 🏷️ **Browse categories** like Trending, Kiss, Hot, and more with one tap
- 🖼️ **Scroll a live GIF grid** — 20 GIFs loaded at a time
- ➕ **Load more** with a single tap at the bottom
- 📤 **Send GIFs** directly into WhatsApp, Telegram, Messages, and any other app that accepts image input

---

## ✨ Features

| Feature | Details |
|---|---|
| 🔤 Inline QWERTY keyboard | Type searches without switching keyboards |
| 🏷️ Category chips | One-tap genre shortcuts (Trending, Kiss, Boobs, Ass, Sex, Blowjob, Fuck, Teen, Hot, Wet, Lick, Cute) |
| 🖼️ GIF grid | 2-column grid, 20 GIFs per page |
| ➕ Load More | Tap button at bottom to load next 20 GIFs |
| 📤 Direct send | GIFs sent via Android `commitContent()` API |
| 🌙 Dark theme | Dark UI designed for keyboard use |
| ⚡ Fast loading | Thumbnail-first loading for speed |

---

## 📁 Project Structure

```
RedGifBoard/
├── .github/workflows/
│   └── build.yml                  ← GitHub Actions — auto builds APK on push
├── app/src/main/
│   ├── java/com/yourname/redgifboard/
│   │   ├── Models.kt              ← Data classes (GifItem, SearchResponse, etc.)
│   │   ├── RedGifsApi.kt          ← Retrofit API client for api.redgifs.com
│   │   ├── GifAdapter.kt          ← RecyclerView adapter with Load More support
│   │   ├── GifKeyboardService.kt  ← THE KEYBOARD — all main logic lives here
│   │   └── MainActivity.kt        ← Setup screen (enable + switch keyboard)
│   ├── res/
│   │   ├── layout/
│   │   │   ├── keyboard_view.xml  ← Main keyboard panel UI
│   │   │   ├── gif_item.xml       ← Single GIF cell in the grid
│   │   │   ├── load_more_item.xml ← "Load More" button row
│   │   │   ├── category_chip.xml  ← Genre chip button
│   │   │   ├── key_button.xml     ← QWERTY key button
│   │   │   └── activity_main.xml  ← Setup screen layout
│   │   ├── xml/
│   │   │   ├── method.xml         ← IME declaration (registers keyboard)
│   │   │   └── file_provider_paths.xml
│   │   └── drawable/
│   │       ├── search_bg.xml      ← Search bar style
│   │       ├── chip_bg.xml        ← Category chip style
│   │       ├── key_bg.xml         ← Keyboard key style
│   │       └── load_more_bg.xml   ← Load more button style
│   └── AndroidManifest.xml
├── gradle.properties
├── build.gradle
├── settings.gradle
└── gradlew
```

---

## 🚀 Building the APK (No PC needed)

This project uses **GitHub Actions** to build the APK automatically in the cloud.

### Steps:
1. Push any change to the `main` branch
2. Go to the **Actions** tab on GitHub
3. Wait ~4–5 minutes for the build to finish (green ✅)
4. Click the build → scroll to **Artifacts** → download **RedGifBoard-v2-debug**
5. Unzip → get `app-debug.apk`

---

## 📲 Installing on Your Phone

1. Transfer `app-debug.apk` to your phone (WhatsApp to yourself, Google Drive, USB cable)
2. On your phone: **Settings → Security → Allow installs from unknown sources** → ON
3. Tap the APK file → **Install**
4. Open the **RedGif Board** app
5. Tap **"Enable Keyboard in Settings"** → find RedGif Board → toggle ON
6. Come back → tap **"Switch to RedGif Board"**
7. Open any chat app → tap a text field → switch keyboard to RedGif Board → enjoy 🎉

---

## 🔧 How It Works

```
User types search
       ↓
GifKeyboardService calls api.redgifs.com/v2/auth/temporary (get token)
       ↓
Calls api.redgifs.com/v2/gifs/search?search_text=...&count=20
       ↓
GifAdapter loads results into 2-column RecyclerView grid using Glide
       ↓
User taps a GIF
       ↓
GIF downloaded to app cache
       ↓
FileProvider shares it via InputConnectionCompat.commitContent()
       ↓
GIF appears in the chat app ✓
```

---

## 🛠️ Tech Stack

| Library | Purpose |
|---|---|
| `Kotlin` | Main language |
| `InputMethodService` | Android IME (keyboard) API |
| `Retrofit2` | HTTP client for RedGifs API |
| `OkHttp3` | Networking + logging |
| `Glide 4` | GIF loading and caching |
| `Coroutines` | Async API calls without freezing UI |
| `RecyclerView` | Scrollable GIF grid |
| `FileProvider` | Secure GIF sharing with other apps |

---

## ⚠️ Notes

- RedGifs API is **unofficial** — no API key required currently
- Some apps (older WhatsApp versions) may not accept keyboard-sent GIFs
- GIFs are temporarily cached in app storage — cleared when app is uninstalled
- Requires Android 8.0+ (API 26)
- This app contains **adult content** — intended for 18+ users only

---

## 🔮 Planned Features (v3)

- [ ] Number row on keyboard
- [ ] Favorites / saved GIFs
- [ ] Cache size management
- [ ] Haptic feedback on key press
- [ ] Switch between GIF grid and keyboard view
- [ ] Dark/light theme toggle

---

## 📄 License

Personal use only. RedGifs content is subject to [RedGifs Terms of Service](https://www.redgifs.com/terms).
