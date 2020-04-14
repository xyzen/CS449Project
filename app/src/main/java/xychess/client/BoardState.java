package xychess.client;

public class BoardState {
    // Board representation
    private String[][] board;

    // Basic movement flags
    private char whose_turn = 'w';
    private String selected_token = "";
    private int selected_rank = 8, selected_file = 8;

    // White En Passant (wep) flags
    private boolean wep_attack_pending = false;
    private int wep_head_rank = 8, wep_head_file = 8, wep_tail_rank = 8, wep_tail_file = 8;

    // Black En Passant (bep) flags
    private boolean bep_attack_pending = false;
    private int bep_head_rank = 8, bep_head_file = 8, bep_tail_rank = 8, bep_tail_file = 8;


}
