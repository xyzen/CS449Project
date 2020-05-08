package xychess.client;

public class BoardState {

    // Board representation
    private String[][] board;

    // Basic turn flag
    private char whose_turn;

    // White En Passant (WEP) flags
    private boolean wep_attack_pending;
    private int wep_head_rank, wep_head_file,
            wep_tail_rank, wep_tail_file;

    // Black En Passant (BEP) flags
    private boolean bep_attack_pending;
    private int bep_head_rank, bep_head_file,
            bep_tail_rank, bep_tail_file;

    // Castling flags
    private boolean wk_moved, wra_moved, wrh_moved,
            wa_cstl_pending, wh_cstl_pending,
            bk_moved, bra_moved, brh_moved,
            ba_cstl_pending, bh_cstl_pending;

    // Endgame flags
    private boolean is_end, checkmate, stalemate;

    public BoardState() {
        newGame();
    }

    public void newGame() {
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
        wk_moved = wra_moved = wrh_moved
                = wa_cstl_pending = wh_cstl_pending
                = bk_moved = bra_moved = brh_moved
                = ba_cstl_pending = bh_cstl_pending
                = false;
        is_end = checkmate = stalemate = false;
    }

    public String getToken(int rank, int file) {
        return board[rank][file];
    }

    public char whoseTurn() {
        return whose_turn;
    }

    private char opponent(char team) {
        return team == 'w' ? 'b' : 'w';
    }

    public boolean submitMove(int old_rank, int old_file, int new_rank, int new_file) {
        resetEnPassant();
        if(!checkMovement(old_rank, old_file, new_rank, new_file, true))
            return false;
        BoardState new_state = commitMove(old_rank, old_file, new_rank, new_file);
        boolean legal_state = !new_state.inCheck(whose_turn);
        if (legal_state) {
            setState(new_state);
            if (assessCheckmate(whose_turn)) {
                is_end = true;
                checkmate = true;
            }
        }
        else
            resetCastlePending();
        return legal_state;
    }

    private void changeTurns() {
        // toggle turn flag
        whose_turn = opponent(whose_turn);
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
        } else if (whose_turn == 'b' && bep_attack_pending) {
            board[wep_head_rank][wep_head_file] = "";
        }
    }

    private void updateCastleFlags(String token, int rank, int file) {
        if (token.equals("wk")) {
            wk_moved = true;
        }
        else if (token.equals("wr")) {
            if (file == 0 && rank == 0)
                wra_moved = true;
            else if (file == 7 && rank == 0)
                wrh_moved = true;
        } else if (token.equals("bk")) {
            bk_moved = true;
        }
        else if (token.equals("br")) {
            if (file == 0 && rank == 7)
                bra_moved = true;
            else if (file == 7 && rank == 7)
                brh_moved = true;
        }
    }

    private void resetCastlePending() {
        if (whose_turn == 'w')
            wa_cstl_pending = wh_cstl_pending = false;
        else
            ba_cstl_pending = bh_cstl_pending = false;
    }

    private void resolveCastling() {
        if (wa_cstl_pending) {
            board[0][0] = "";
            board[0][3] = "wr";
            wa_cstl_pending = false;
        } else if (wh_cstl_pending) {
            board[0][7] = "";
            board[0][5] = "wr";
            wh_cstl_pending = false;
        } else if (ba_cstl_pending) {
            board[7][0] = "";
            board[7][3] = "br";
            ba_cstl_pending = false;
        } else if (bh_cstl_pending) {
            board[7][7] = "";
            board[7][5] = "br";
            bh_cstl_pending = false;
        }
    }

    private BoardState commitMove(int old_rank, int old_file, int new_rank, int new_file) {
        BoardState new_state = new BoardState();
        new_state.setState(this);
        String token = new_state.board[old_rank][old_file];
        new_state.board[old_rank][old_file] = "";
        new_state.board[new_rank][new_file] = token;
        new_state.resolveCastling();
        new_state.updateCastleFlags(token, old_rank, old_file);
        new_state.resolveEnPassant();
        new_state.changeTurns();
        return new_state;
    }

    private boolean assessCheckmate(char team) {
        BoardState new_state = new BoardState();
        String token;
        if (inCheck(team)) {
            for (int r1 = 0; r1 < 8; r1++) {
                for (int f1 = 0; f1 < 8; f1++) {
                    for (int r2 = 0; r2 < 8; r2++) {
                        for (int f2 = 0; f2 < 8; f2++) {
                            token = board[r1][f1];
                            if (token.equals("") || token.charAt(0) != team)
                                continue;
                            new_state.setState(this);
                            new_state.resetEnPassant();
                            if (new_state.checkMovement(r1, f1, r2, f2, true)) {
                                new_state = new_state.commitMove(r1, f1, r2, f2);
                                if (!new_state.inCheck(team))
                                    return false;
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    private boolean underAttack(char team, int rank, int file) {
        String token;
        char color;
        char opponent = opponent(team);
        for (int r = 0; r < 8; r++) {
            for (int f = 0; f < 8; f++) {
                token = board[r][f];
                if (token.equals(""))
                    continue;
                color = token.charAt(0);
                if (color == opponent && checkMovement(r, f, rank, file, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean inCheck(char team) {
        int[] rank_file = getKingRankFile(team);
        int king_rank = rank_file[0],
                king_file = rank_file[1];
        return underAttack(team, king_rank, king_file);
    }

    private int[] getKingRankFile(char team) {
        // Get appropriate King token
        String target;
        if (team == 'w')
            target = "wk";
        else
            target = "bk";
        int[] rank_file = {8,8};
        for (int r = 0; r < board.length; r++) {
            for (int f = 0; f < board[r].length; f++) {
                if (board[r][f].equals(target)) {
                    rank_file = new int[] {r, f};
                }
            }
        }
        return rank_file;
    }

    private void setState(BoardState other) {
        for (int r = 0; r < board.length; r++) {
            for (int f = 0; f < board[r].length; f++) {
                board[r][f] = other.board[r][f];
            }
        }
        whose_turn = other.whose_turn;
        wep_attack_pending = other.wep_attack_pending;
        bep_attack_pending = other.bep_attack_pending;
        wep_head_rank = other.wep_head_rank;
        wep_head_file = other.wep_head_file;
        wep_tail_rank = other.wep_tail_rank;
        wep_tail_file = other.wep_tail_file;
        bep_head_rank = other.bep_head_rank;
        bep_head_file = other.bep_head_file;
        bep_tail_rank = other.bep_tail_rank;
        bep_tail_file = other.bep_tail_file;
        wk_moved = other.wk_moved;
        wra_moved = other.wra_moved;
        wrh_moved = other.wrh_moved;
        wa_cstl_pending = other.wa_cstl_pending;
        wh_cstl_pending = other.wh_cstl_pending;
        bk_moved = other.bk_moved;
        bra_moved = other.bra_moved;
        brh_moved = other.brh_moved;
        ba_cstl_pending = other.ba_cstl_pending;
        bh_cstl_pending = other.bh_cstl_pending;
        is_end = other.is_end;
        checkmate = other.checkmate;
        stalemate = other.stalemate;
    }

    private boolean checkMovement(int old_rank, int old_file, int new_rank, int new_file, boolean flagging) {
        // Filter movements with same origin and destination
        if (new_rank == old_rank && new_file == old_file) {
            return false;
        }
        // Get tokens from old cell and new cell
        String moved_token = board[old_rank][old_file],
                taken = board[new_rank][new_file];
        // Get team indicators
        char moved_color = moved_token.charAt(0);
        // Filter player taking their own piece
        if (!taken.equals("") && moved_color == taken.charAt(0)) {
                return false;
        }
        // Filter based on type of piece
        char piece_type = moved_token.charAt(1);
        switch(piece_type) {
            case('p'):
                if (!checkPawnMove(old_rank, old_file, new_rank, new_file, taken, moved_color, flagging)) {
                    return false;
                }
                break;
            case('b'):
                if (!checkBishopMove(old_rank, old_file, new_rank, new_file)) {
                    return false;
                }
                break;
            case('n'):
                if (!checkKnightMove(old_rank, old_file, new_rank, new_file)) {
                    return false;
                }
                break;
            case('r'):
                if (!checkRookMove(old_rank, old_file, new_rank, new_file)) {
                    return false;
                }
                break;
            case('q'):
                if (!checkQueenMove(old_rank, old_file, new_rank, new_file)) {
                    return false;
                }
                break;
            case('k'):
                if (!checkKingMove(old_rank, old_file, new_rank, new_file)) {
                    return false;
                }
                break;
        }
        return true;
    }

    private boolean checkPawnMove(int old_rank, int old_file, int new_rank, int new_file, String taken, char team, boolean flagging) {
        // Get direction for current team
        int forward = team == 'w' ? 1 : -1;
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
            int start_rank = team == 'w' ? 1 : 6;
        // 2. First move; two spaces
            if (old_rank == start_rank && rank_diff == 2 * forward
                    && board[old_rank+forward][old_file].equals("")) {
                if (flagging) {
                    // Set appropriate en passant flags
                    if (team == 'w') {
                        wep_head_rank = new_rank;
                        wep_head_file = new_file;
                        wep_tail_rank = new_rank - forward;
                        wep_tail_file = new_file;
                    } else {
                        bep_head_rank = new_rank;
                        bep_head_file = new_file;
                        bep_tail_rank = new_rank - forward;
                        bep_tail_file = new_file;
                    }
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
            // Check and set appropriate en passant flags
            if (team == 'w' && new_rank == bep_tail_rank && new_file == bep_tail_file) {
                if (flagging)
                    wep_attack_pending = true;
                return true;
            } else if (team == 'b' && new_rank == wep_tail_rank && new_file == wep_tail_file) {
                if (flagging)
                    bep_attack_pending = true;
                return true;
            }
        }
        return false;
    }

    private boolean checkBishopMove(int old_rank, int old_file, int new_rank, int new_file) {
        return clearDiagonal(old_rank, old_file, new_rank, new_file);
    }

    private boolean checkKnightMove(int old_rank, int old_file, int new_rank, int new_file) {
        // Get distance of traversal along axes
        int rank_diff = Math.abs(new_rank - old_rank),
                file_diff = Math.abs(new_file - old_file);
        // Check for a 2x1 or 1x2 traversal
        return (rank_diff == 1 && file_diff == 2)
                || (rank_diff == 2 && file_diff == 1);
    }

    private boolean checkRookMove(int old_rank, int old_file, int new_rank, int new_file) {
        return clearRankOrFile(old_rank, old_file, new_rank, new_file);
    }

    private boolean checkQueenMove(int old_rank, int old_file, int new_rank, int new_file) {
        return clearDiagonal(old_rank, old_file, new_rank, new_file)
                || clearRankOrFile(old_rank, old_file, new_rank, new_file);
    }

    private boolean checkKingMove(int old_rank, int old_file, int new_rank, int new_file) {
        // Get distance of traversal along axes
        int rank_diff = Math.abs(new_rank - old_rank),
                file_diff = Math.abs(new_file - old_file);
        // Check for 1x0, 0x1, or 1x1 traversal
        if ((rank_diff == 1 && file_diff == 0)
                || (rank_diff == 0 && file_diff == 1)
                || (rank_diff == 1 && file_diff == 1))
            return true;
        // Check for castling moves
        return checkCastling(old_rank, old_file, new_rank, new_file);
    }

    private boolean checkCastling(int old_rank, int old_file, int new_rank, int new_file) {
        // Filter moves from wrong file
        if (old_file != 4)
            return false;
        // Filter moves to wrong file
        if (new_file != 2 && new_file != 6)
            return false;;
        // Filter by team, rank, flag, and file
        if (whose_turn == 'w') {
            if (wk_moved || old_rank != 0 || new_rank != 0)
                return false;
            if (new_file == 2) {
                if (wra_moved)
                    return false;
                wa_cstl_pending = true;
            }
            if (new_file == 6) {
                if (wrh_moved)
                    return false;
                wh_cstl_pending = true;
            }
        } else {
            if (bk_moved || old_rank != 7 || new_rank != 7)
                return false;
            if (new_file == 2) {
                if (bra_moved)
                    return false;
                ba_cstl_pending = true;
            }
            if (new_file == 6) {
                if (brh_moved)
                    return false;
                bh_cstl_pending = true;
            }
        }
        // Get file step
        int file_diff = new_file - old_file;
        int file_step = file_diff == 0 ? 0 : file_diff < 0 ? -1 : 1;
        // Filter unsafe Kings
        int r = old_rank, f = old_file;
        if (underAttack(whose_turn, r, f))
            return false;
        f += file_step;
        // Check that all intermediate squares are empty and safe
        String token;
        while (f != 0 && f != 7) {
            token = board[r][f];
            if (!token.equals("") || underAttack(whose_turn, r, f))
                return false;
            f += file_step;
        }
        // Check that rook is not under attack
        if (underAttack(whose_turn, r, f))
            return false;
        return true;
    }

    private boolean clearDiagonal(int old_rank, int old_file, int new_rank, int new_file) {
        // Get number of ranks and files traversed
        int rank_diff = new_rank - old_rank,
                file_diff = new_file - old_file;
        // Check that same number of ranks and files traversed
        if (Math.abs(rank_diff) != Math.abs(file_diff)) {
            // Movement isn't diagonal. Move blocked!
            return false;
        }
        // Get direction of traversal along each axis (-1 or 1)
        int rank_sign = rank_diff < 0 ? -1 : 1,
                file_sign = file_diff < 0 ? -1 : 1;
        // Start at the old cell
        int r = old_rank + rank_sign,
                f = old_file + file_sign;
        // Check each cell between
        while (r != new_rank || f != new_file) {
            // Check this cell
            if (!board[r][f].equals("")) {
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

    private boolean clearRankOrFile(int old_rank, int old_file, int new_rank, int new_file) {
        // Check that movement is along one rank or file
        int rank_diff = new_rank - old_rank,
                file_diff = new_file - old_file;
        if (rank_diff != 0 && file_diff != 0) {
            // Movement is along both axes. Move blocked!
            return false;
        }
        // Get direction of movement along each axis (-1, 0, or 1)
        int rank_step = rank_diff == 0 ? 0 : rank_diff < 0 ? -1 : 1,
                file_step = file_diff == 0 ? 0 : file_diff < 0 ? -1 : 1;
        // Start after old cell
        int r = old_rank + rank_step,
                f = old_file + file_step;
        // Check that each intermediate cell is empty
        while (r != new_rank || f != new_file) {
            // Check this cell
            if (!board[r][f].equals("")) {
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
