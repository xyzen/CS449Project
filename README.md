# xyChess
  A simple chess app for Android

## Setup
1. Unzip ProjectFolder from ZIP file OR clone repository (https://github.com/xyzen/xyChess)
2. Open ProjectFolder in Android Studio
3. Enable a Pixel 2 API 29 emulator
4. Run 'app' from Android Studio using emulator
5. Demonstrate features with instructions below

## Feature Demos
* Movement restriction
  * Try moving out of turn, making illegal moves, moving into check, etc.
* En Passant
  * When a pawn moves *through* the attack zone of an opposite pawn, it can be taken.
  * Example: 1. e4 a5 2. e5 d5 3. pxd6
* Castling
  * If a king and rook have not been moved, and each space between them is clear, they can perform a special move known as 'castling,' provided that neither they nor the spaces between them are 'threatened.'
  * Example: 1. e4 e5 2. Bc4 Bc5 3. Nf3 Nf6 4. Kg1 Kg8 (denoted: 4. O-O O-O)
