package xychess.client;

public class BoardState {
    // Board representation
    private String[][] board;

    // Basic turn flag
    private char whose_turn;

    // White En Passant (wep) flags
    private boolean wep_attack_pending;
    private int wep_head_rank, wep_head_file,
            wep_tail_rank, wep_tail_file;

    // Black En Passant (bep) flags
    private boolean bep_attack_pending;
    private int bep_head_rank, bep_head_file,
            bep_tail_rank, bep_tail_file;

    public void init() {
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
        whose_turn = 'w';
        wep_attack_pending = bep_attack_pending = false;
        wep_head_rank = wep_head_file
                = wep_tail_rank = wep_tail_file
                = bep_head_rank = bep_head_file
                = bep_tail_rank = bep_tail_file
                = 8;
    }

    public String getRankFile(int rank, int file) {
        return board[rank][file];
    }

    public char whoseTurn() {
        return whose_turn;
    }

    private void changeTurns() {
        // toggle turn flag
        whose_turn = whose_turn == 'w' ? 'b' : 'w';
    }

    private void resetEnPassant() {
        if (whose_turn == 'w') {
            wep_attack_pending = false;
            wep_head_rank = wep_head_file
                    = wep_tail_rank = wep_tail_file
                    = 8;
        } else {
            bep_attack_pending = false;
            bep_head_rank = bep_head_file
                    = bep_tail_rank = bep_tail_file
                    = 8;
        }
    }

    private void resolveEnPassant() {
        if (whose_turn == 'w' && wep_attack_pending) {
            board[bep_head_rank][bep_head_file] = "";
        } else if (whose_turn == 'b' && bep_attack_pending){
            board[wep_head_rank][wep_head_file] = "";
        }
    }

    private boolean checkMovement(int old_rank, int old_file, int new_rank, int new_file, BoardState state) {
        // Filter movements with same origin and destination
        if (new_rank == old_rank && new_file == old_file) {
            return false;
        }
        // Get tokens from old cell and new cell
        String moved_token = state.board[old_rank][old_file],
                state.taken = state.board[new_rank][new_file];
        // Filter player taking their own piece
        if (!taken.equals("")) {
            char moved_color = moved_token.charAt(0),
                    taken_color = taken.charAt(0);
            if (moved_color == taken_color) {
                return false;
            }
        }
        // Filter based on type of piece
        char piece_type = moved_token.charAt(1);
        switch(piece_type) {
            case('p'):
                if (!checkPawnMove(old_rank, old_file, new_rank, new_file, taken, state)) {
                    return false;
                }
                break;
            case('b'):
                if (!checkBishopMove(old_rank, old_file, new_rank, new_file, state)) {
                    return false;
                }
                break;
            case('n'):
                if (!checkKnightMove(old_rank, old_file, new_rank, new_file, state)) {
                    return false;
                }
                break;
            case('r'):
                if (!checkRookMove(old_rank, old_file, new_rank, new_file, state)) {
                    return false;
                }
                break;
            case('q'):
                if (!checkQueenMove(old_rank, old_file, new_rank, new_file, state)) {
                    return false;
                }
                break;
            case('k'):
                if (!checkKingMove(old_rank, old_file, new_rank, new_file, state)) {
                    return false;
                }
                break;
        }
        return true;
    }

    private boolean checkPawnMove(int old_rank, int old_file, int new_rank, int new_file, String taken, BoardState state) {
        // Get direction for current team
        int forward = state.whose_turn == 'w' ? 1 : -1;
        // Get distances traveled
        int rank_diff = new_rank - old_rank,
                file_diff = Math.abs(new_file - old_file);
        // Check for 4 cases:
        if (file_diff == 0 && taken.equals("")) {
        // 1. One space forward
            if (rank_diff == forward) {
                return true;
            }
            // Get starting rank
            int start_rank = state.whose_turn == 'w' ? 1 : 6;
        // 2. First move; two spaces
            if (old_rank == start_rank && rank_diff == 2 * forward
                    && clearRankOrFile(new_rank, new_file)) {
                // Set appropriate en passant flags
                if (whose_turn == 'w') {
                    state.wep_head_rank = new_rank;
                    state.wep_head_file = new_file;
                    state.wep_tail_rank = new_rank-forward;
                    state.wep_tail_file = new_file;
                } else {
                    state.bep_head_rank = new_rank;
                    state.bep_head_file = new_file;
                    state.bep_tail_rank = new_rank-forward;
                    state.bep_tail_file = new_file;
                }
                return true;
            }
        }
        // 3. Normal diagonal attack
        if (file_diff == 1 && rank_diff == forward && !taken.equals("")) {
            return true;
        }
        // 4. En passant attack
        if (file_diff == 1 && rank_diff == forward && taken.equals("")) {
            // Set appropriate en passant flags
            if (state.whose_turn == 'w' && new_rank == state.bep_tail_rank && new_file == state.bep_tail_file) {
                wep_attack_pending = true;
                return true;
            } else if (state.whose_turn == 'b' && new_rank == state.wep_tail_rank && new_file == state.wep_tail_file) {
                state.bep_attack_pending = true;
                return true;
            }
        }
        return false;
    }

    private boolean checkBishopMove(int rank, int file) {
        return clearDiagonal(rank, file);
    }

    private boolean checkKnightMove(int rank, int file) {
        // Get distance of traversal along axes
        int rank_diff = Math.abs(rank - origin_rank),
                file_diff = Math.abs(file - origin_file);
        // Check for a 2x1 or 1x2 traversal
        return (rank_diff == 1 && file_diff == 2)
                || (rank_diff == 2 && file_diff == 1);
    }

    private boolean checkRookMove(int rank, int file) {
        return clearRankOrFile(rank, file);
    }

    private boolean checkQueenMove(int rank, int file) {
        return clearDiagonal(rank, file) || clearRankOrFile(rank, file);
    }

    private boolean checkKingMove(int rank, int file) {
        // Get distance of traversal along axes
        int rank_diff = Math.abs(rank - origin_rank),
                file_diff = Math.abs(file - origin_file);
        // Check for 1x0, 0x1, or 1x1 traversal
        return (rank_diff == 1 && file_diff == 0)
                || (rank_diff == 0 && file_diff == 1)
                || (rank_diff == 1 && file_diff == 1);
    }

    private boolean clearDiagonal(int rank, int file) {
        // Get number of ranks and files traversed
        int rank_diff = rank - origin_rank,
                file_diff = file - origin_file;
        // Check that same number of ranks and files traversed
        if (Math.abs(rank_diff) != Math.abs(file_diff)) {
            // Movement isn't diagonal. Move blocked!
            return false;
        }
        // Get direction of traversal along each axis (-1 or 1)
        int rank_sign = rank_diff < 0 ? -1 : 1,
                file_sign = file_diff < 0 ? -1 : 1;
        // Start at the origin cell
        int r = origin_rank,
                f = origin_file;
        // Check each cell between
        while (r != rank || f != file) {
            // Check this cell
            if (!state[r][f].equals("")) {
                // Cell isn't empty! Move blocked!
                return false;
            }
            // Traverse one cell along each axis
            r += rank_sign;
            f += file_sign;
        }
        // All cells between were empty.
        return true;
    }

    private boolean clearRankOrFile(int rank, int file) {
        // Check that movement is along one rank or file
        int rank_diff = rank - origin_rank,
                file_diff = file - origin_file;
        if (rank_diff != 0 && file_diff != 0) {
            // Movement is along both axes. Move blocked!
            return false;
        }
        // Get direction of movement along each axis (-1, 0, or 1)
        int rank_step = rank_diff == 0 ? 0 : rank_diff < 0 ? -1 : 1,
                file_step = file_diff == 0 ? 0 : file_diff < 0 ? -1 : 1;
        // Start at origin cell
        int r = origin_rank,
                f = origin_file;
        // Check that each intermediate cell is empty
        while (r != rank || f != file) {
            // Check this cell
            if (!state[r][f].equals("")) {
                // Cell isn't empty! Move blocked!
                return false;
            }
            // Traverse to next cell
            r += rank_step;
            f += file_step;
        }
        // All intermediate cells were empty.
        return true;
    }
}
