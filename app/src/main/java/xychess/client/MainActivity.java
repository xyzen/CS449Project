package xychess.client;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    BoardState board_state = new BoardState();
    private boolean selection_pending;
    private int selected_rank, selected_file;

    public MainActivity() {
        selection_pending = false;
        selected_rank = selected_file = 8;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initialize board
        newGame();
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

    public void newGameDialog(android.view.View view) {
        ConfirmNewGameDialog cngd = new ConfirmNewGameDialog(this);
        cngd.show(getSupportFragmentManager(), "confirm");
    }

    public void newGame() {
        board_state = new BoardState();
        refreshBoardView();
        refreshTurnView();
    }

    private void refreshTurnView() {
        if (board_state.whoseTurn() == 'w') {
            setTurnView("White to Move");
        } else {
            setTurnView("Black to Move");
        }
    }

    private void setTurnView(String text) {
        ((TextView) findViewById(R.id.turn_indicator))
                .setText(text);
    }

    private void refreshBoardView() {
        String piece;
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                piece = board_state.getToken(rank, file);
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

    private int[] getCellRankFile(int view_id) {
        int[] rank_file = { 8, 8 };
        for (int r = 0; r < 8; r++) {
            for (int f = 0; f < 8; f++) {
                if (cells[r][f] == view_id) {
                    rank_file[0] = r;
                    rank_file[1] = f;
                    break;
                }
            }
        }
        return rank_file;
    }

    public void selectCell(android.view.View view) {
        int[] rank_file = getCellRankFile(view.getId());
        int rank = rank_file[0], file = rank_file[1];
        if (selection_pending) {
            board_state.submitMove(selected_rank, selected_file, rank, file);
            refreshBoardView();
            if (board_state.isCheckmate()) {
                String message = board_state.whoseTurn() == 'w' ? "Black" : "White";
                message = "Checkmate! " + message + " wins";
                CheckmateDialog chkmt = new CheckmateDialog(message);
                setTurnView(message);
                chkmt.show(getSupportFragmentManager(), "checkmate");
            } else if (board_state.isStalemate()) {;
                CheckmateDialog stlmt = new CheckmateDialog("Stalemate!");
                setTurnView("Stalemate!");
                stlmt.show(getSupportFragmentManager(), "stalemate");
            } else {
                refreshTurnView();
            }
            selection_pending = false;
        }
        else {
            String token = board_state.getToken(rank, file);
            // Check for empty selections or out-of-turn moves
            if (!token.equals("") && token.charAt(0) == board_state.whoseTurn()) {
                refreshCellView(rank, file, 0);
                selected_rank = rank;
                selected_file = file;
                selection_pending = true;
            }
        }
    }
}
