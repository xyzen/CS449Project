# xyChess
  A simple chess app for Android

## Setup
1. Unzip xyChess folder from xyChess.zip OR clone repository (https://github.com/xyzen/xyChess)
2. Open xyChess folder in Android Studio
3. Enable a Pixel 2 API 29 emulator
4. Run 'app' from Android Studio using emulator
5. Demonstrate features with instructions below

## Feature Demos
* New Game
    * Press the New Game button and select, "Yes."
* Move indicator
    * Make a move, and notice how the text below the board updates.
* Movement restriction
    * Try moving out of turn, making illegal moves, moving into check, etc.
* En Passant
    * When a pawn's first move passes *through* the attack zone of an opposing pawn, it can be taken.
    * Example: 1. e4 a5 2. e5 d5 3. pxd6
* Castling
    * If a king and rook have not been moved, and each space between them is clear, they can perform a special move known as 'castling,' provided that neither they nor the spaces between them are 'threatened.'
    * Example: 1. e4 e5 2. Bc4 Bc5 3. Nf3 Nf6 4. Kg1 Kg8 (denoted: 4. O-O O-O)
* Check
    * If a player's king is under attack, the text below will be updated to say, "Check!"
    * Example: 1. e4 e5 2. f4 Qh4! (White is in Check)
* Checkmate
    * When a player is in check and has no move available to escape it, they have been checkmated.
    * Fool's Mate: 1. f3 e5 2. g4 Qh4# (Black Wins)
    * Scholar's Mate 1: 1. e4 e5 2. Bc4 Nc6 3. Qh5 Nf6 4. Qxf7# (White Wins)
    * Scholar's Mate 2: 1. e4 e5 2. Bc4 Nc6 3. Qf3 d6 4. Qxf7# (White Wins)
* Stalemate
    * When a player is NOT in check and has no move available to escape it, the game is in stalemate.
    * Example: 1. e3 a5 2. Qh5 Ra6 3. Qxa5 h5 4. h4 Rah6 5. Qxc7 f6 6. Qxd7+ Kf7 7. Qxb7 Qd3 8. Qxb8 Qh7 9. Qxc8 Kg6 10. Qe6
    * Not Yet Implemented: Stalemate by insufficient material, 3 identical moves, or 50 moves after pawn activity.
