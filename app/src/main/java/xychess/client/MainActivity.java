package xychess.client;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private char turn = 'w';
    private String[][] board;
    private String selected_token = "";
    private int selected_rank = 8, selected_file = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initialize board
        initBoard();
    }

    private static final java.util.HashMap<String, Integer>
            // mapping from string to int
            tokenImg = new java.util.HashMap<>();
    static {
        // mapping from piece token to drawable image resource
        tokenImg.put("wr", R.drawable.white_rook);
        tokenImg.put("wn", R.drawable.white_knight);
        tokenImg.put("wb", R.drawable.white_bishop);
        tokenImg.put("wq", R.drawable.white_queen);
        tokenImg.put("wk", R.drawable.white_king);
        tokenImg.put("wp", R.drawable.white_pawn);
        tokenImg.put("br", R.drawable.black_rook);
        tokenImg.put("bn", R.drawable.black_knight);
        tokenImg.put("bb", R.drawable.black_bishop);
        tokenImg.put("bq", R.drawable.black_queen);
        tokenImg.put("bk", R.drawable.black_king);
        tokenImg.put("bp", R.drawable.black_pawn);
    }

    private static final int[][] cells = {
            // board UI components / ImageViews
            // *should* appear upside-down here
            { R.id.cell_a0, R.id.cell_b0, R.id.cell_c0, R.id.cell_d0, R.id.cell_e0, R.id.cell_f0, R.id.cell_g0, R.id.cell_h0 },
            { R.id.cell_a1, R.id.cell_b1, R.id.cell_c1, R.id.cell_d1, R.id.cell_e1, R.id.cell_f1, R.id.cell_g1, R.id.cell_h1 },
            { R.id.cell_a2, R.id.cell_b2, R.id.cell_c2, R.id.cell_d2, R.id.cell_e2, R.id.cell_f2, R.id.cell_g2, R.id.cell_h2 },
            { R.id.cell_a3, R.id.cell_b3, R.id.cell_c3, R.id.cell_d3, R.id.cell_e3, R.id.cell_f3, R.id.cell_g3, R.id.cell_h3 },
            { R.id.cell_a4, R.id.cell_b4, R.id.cell_c4, R.id.cell_d4, R.id.cell_e4, R.id.cell_f4, R.id.cell_g4, R.id.cell_h4 },
            { R.id.cell_a5, R.id.cell_b5, R.id.cell_c5, R.id.cell_d5, R.id.cell_e5, R.id.cell_f5, R.id.cell_g5, R.id.cell_h5 },
            { R.id.cell_a6, R.id.cell_b6, R.id.cell_c6, R.id.cell_d6, R.id.cell_e6, R.id.cell_f6, R.id.cell_g6, R.id.cell_h6 },
            { R.id.cell_a7, R.id.cell_b7, R.id.cell_c7, R.id.cell_d7, R.id.cell_e7, R.id.cell_f7, R.id.cell_g7, R.id.cell_h7 }
    };

    public void newGame(android.view.View view) {
        ConfirmNewGameDialog cngd = new ConfirmNewGameDialog(this);
        cngd.show(getSupportFragmentManager(), "confirm");
    }

    protected void initBoard() {
        // no View is attached to this call
        // --> 0 is just a garbage value
        initBoard(findViewById(0));
    }

    private void initBoard(android.view.View view) {
        board = new String[][]{
                // initial board piece tokens
                // *should* appear upside-down here
                {"wr", "wn", "wb", "wq", "wk", "wb", "wn", "wr"},
                {"wp", "wp", "wp", "wp", "wp", "wp", "wp", "wp"},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"bp", "bp", "bp", "bp", "bp", "bp", "bp", "bp"},
                {"br", "bn", "bb", "bq", "bk", "bb", "bn", "br"}
        };
        // refresh UI
        refreshBoardView();
        refreshTurnView("White to Move");
        // reinitialize data values
        selected_token = "";
        turn = 'w';
    }

    private void changeTurns() {
        // toggle turn char and refresh turn view
        if (turn == 'w') {
            turn = 'b';
            refreshTurnView("Black to Move");
        }
        else {
            turn = 'w';
            refreshTurnView("White to Move");
        }
    }

    private void refreshTurnView(String text) {
        ((TextView) findViewById(R.id.turn_indicator))
                .setText(text);
    }

    private void refreshBoardView() {
        String piece;
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                piece = board[rank][file];
                if (tokenImg.containsKey(piece)) {
                    refreshCellView(rank, file, tokenImg.get(piece));
                }
                else {
                    refreshCellView(rank, file, 0);
                }
            }
        }
    }

    private void refreshCellView(int rank, int file, Integer drawable) {
        ((ImageView) findViewById(cells[rank][file]))
                .setImageResource(drawable);
    }

    public void selectCell(android.view.View view) {
        int view_id = view.getId();
        int rank = 8, file = 8;
        for (int r = 0; r < 8; r++) {
            for (int f = 0; f < 8; f++) {
                if (cells[r][f] == view_id) {
                    rank = r;
                    file = f;
                    break;
                }
            }
        }
        if (rank == 8 || file == 8) {
            return;
        }
        if (selected_token == "") {
            String token = board[rank][file];
            if (token == "" || token.charAt(0) != turn) {
                return;
            }
            refreshCellView(rank, file, 0);
            selected_token = token;
            board[rank][file] = "";
            selected_rank = rank;
            selected_file = file;
        }
        else if (tokenImg.containsKey(selected_token)) {
            if (checkMove(rank, file)) {
                changeTurns();
            }
            else {
                rank  = selected_rank;
                file  = selected_file;
            }
            refreshCellView(rank, file, tokenImg.get(selected_token));
            board[rank][file] = selected_token;
            selected_token = "";
            selected_rank  = 8;
            selected_file  = 8;
        }
    }

    private boolean checkMove(int rank, int file) {
        // Check that piece was not placed in its original position
        if (rank == selected_rank && file == selected_file) {
            return false;
        }
        // Check that player is not taking their own piece
        String taken = board[rank][file];
        if (taken != "") {
            char moved_color = selected_token.charAt(0),
                    taken_color = taken.charAt(0);
            if (moved_color == taken_color) {
                return false;
            }
        }
        // Check moves based on type of piece
        char piece_type = selected_token.charAt(1);
        switch(piece_type) {
            case('p'):
                if (!checkPawnMove(rank, file, taken)) {
                    return false;
                }
                break;
            case('b'):
                if (!checkBishopMove(rank, file)) {
                    return false;
                }
                break;
            case('n'):
                if (!checkKnightMove(rank, file)) {
                    return false;
                }
                break;
            case('r'):
                if (!checkRookMove(rank, file)) {
                    return false;
                }
                break;
            case('q'):
                if (!checkQueenMove(rank, file)) {
                    return false;
                }
                break;
            case('k'):
                if (!checkKingMove(rank, file)) {
                    return false;
                }
                break;
        }
        // TODO: Check that resulting state
        //  does not cause self-check
        return true;
    }

    private boolean checkPawnMove(int rank, int file, String taken) {
        return true;
    }

    private boolean checkBishopMove(int rank, int file) {
        int rank_diff = Math.abs(rank - selected_rank);
        int file_diff = Math.abs(file - selected_file);
        if (rank_diff != file_diff) {
            return false;
        }
        if (clearDiagonal(rank, file)) {
            return true;
        }
        return false;
    }

    private boolean checkKnightMove(int rank, int file) {
        return true;
    }

    private boolean checkRookMove(int rank, int file) {
        return true;
    }

    private boolean checkQueenMove(int rank, int file) {
        return true;
    }

    private boolean checkKingMove(int rank, int file) {
        return true;
    }

    private boolean clearDiagonal(int rank, int file) {
        int rank_diff = rank - selected_rank,
                file_diff = file - selected_file;
        int rank_sign = rank_diff < 0 ? -1 : 1,
                file_sign = file_diff < 0 ? -1 : 1;
        int r = selected_rank + rank_sign,
                f = selected_file + file_sign;
        while (r != rank && f != file) {
            if (board[r][f] != "") {
                return false;
            }
            r += rank_sign;
            f += file_sign;
        }
        return true;
    }

    private boolean clearPath(int rank, int file) {
        return true;
    }
}
