package xychess.client;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    BoardState board_state;
    private boolean selection_pending = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initialize board
        initBoard(findViewById(0));
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

    private void initBoard(android.view.View view) {
        board_state.init();
        refreshBoardView();
        refreshTurnView("White to Move");
    }

    private void refreshTurnView(String text) {
        ((TextView) findViewById(R.id.turn_indicator))
                .setText(text);
    }

    private void refreshBoardView() {
        String piece;
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                piece = board_state.getRankFile(rank, file);
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
        if (rank == 8) {
            return;
        }
        if (!selection_pending) {
            String token = board_state.getRankFile(rank, file);
            // Check for empty selections or out-of-turn moves
            if (token.equals("") || token.charAt(0) != board_state.whoseTurn()) {
                return;
            }
            selection_pending = !selection_pending;
        }
        else {
            // Filter illegal movements and self-check
            if (checkMovement(rank, file, board)) {
                resolveEnPassant();
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
            selection_pending = !selection_pending;
        }
    }
}
