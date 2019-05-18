package AI.ConnectFour;

import AI.ConnectFour.Player.AIConnectFourPlayer;
import AI.ConnectFour.Player.ConnectFourPlayer;
import AI.ConnectFour.Player.HumanConnectFourPlayer;
import AI.ConnectFour.Player.RandomConnectFourPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayConnectFour extends JFrame {

    public static final int SIZE = 7;

    private ConnectFourPlayer player1;
    private ConnectFourPlayer player2;

    private int currentPlayer = 0;

    private JButton[] gridButtons;

    private List<EndGameListener> endGameListeners = new ArrayList<>();

    public PlayConnectFour(boolean headless) {

        if (headless) {
            System.setProperty("java.awt.headless", "true");
        }

        this.setSize(350, 400);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(!headless);
        this.setResizable(true);

        player1 = new HumanConnectFourPlayer();
        player2 = new RandomConnectFourPlayer();

        gridButtons = new JButton[SIZE*SIZE];
    }

    public static void main(String[] args) {
        boolean headless = false;
        PlayConnectFour game = new PlayConnectFour(headless);
        ConnectFourPlayer random1 = new HumanConnectFourPlayer();
        AIConnectFourPlayer random2 = new AIConnectFourPlayer();
        try {
            random2.loadNetwork(new File("res/network/connect-four-98.net"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        game.setPlayer1(random1);
        game.setPlayer2(random2);

        game.renderGUI();
        game.start();
    }

    void setPlayer1(ConnectFourPlayer player) {
        player1 = player;
        player1.setGame(this);
        player1.setPlayerId(1);
        player1.init();
    }

    void setPlayer2(ConnectFourPlayer player) {
        player2 = player;
        player2.setGame(this);
        player2.setPlayerId(2);
        player2.init();
    }

    void addEndGameListener(EndGameListener listener) {
        endGameListeners.add(listener);
    }

    void renderGUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel game = new JPanel(new GridLayout(SIZE, SIZE));

        this.add(mainPanel);

        mainPanel.add(game, BorderLayout.SOUTH);

        for (int i = 0; i < SIZE*SIZE; i++) {

            gridButtons[i] = new JButton();
            gridButtons[i].setText(" ");
            gridButtons[i].setVisible(true);
            gridButtons[i].setEnabled(true);
            gridButtons[i].addActionListener(new MyActionListener(i));
            game.add(gridButtons[i]);
        }
        this.revalidate();

        game.setVisible(true);
    }

    private ConnectFourPlayer getCurrentPlayer() {
        return this.getPlayer(currentPlayer);
    }

    private ConnectFourPlayer getPlayer(int playerId) {
        if(playerId == 0) {
            return player1;
        }
        return player2;
    }

    private String getPlayerSign(int player) {
        if (player == 1) {
            return "O";
        }
        return "X";
    }

    void start() {
        ConnectFourPlayer currentPlayer = this.getCurrentPlayer();
        int[][] state = this.getBoardState();
        currentPlayer.requestMove(state);
    }

    private int[][] getBoardState() {
        int[][] state = new int[SIZE][SIZE];

        for (int i = 0; i < SIZE*SIZE; i++) {
            int x = i % SIZE;
            int y = i / SIZE;
            String text = gridButtons[i].getText();
            switch(text) {
                case "O":
                    state[y][x] = 1;
                    break;
                case "X":
                    state[y][x] = 2;
                    break;
                default:
                    state[y][x] = 0;
                    break;
            }
        }
        return state;
    }

    public String boardToString(int[][] a) {
        return Arrays.deepToString(a).replaceAll("], ", "]\n").substring(1).replaceAll(", ", ",\t");
    }

    private boolean checkWinningCondition(int player) {
        int[][] state = this.getBoardState();

        // Source: https://stackoverflow.com/a/38211417

        // verticalCheck
        for (int i = 0; i<SIZE-3 ; i++ ){
            for (int j = 0; j<SIZE; j++){
                if (state[i][j] == player && state[i+1][j] == player && state[i+2][j] == player && state[i+3][j] == player){
                    return true;
                }
            }
        }
        // horizontalCheck
        for (int j = 0; j<SIZE-3 ; j++ ){
            for (int i = 0; i<SIZE; i++){
                if (state[i][j] == player && state[i][j+1] == player && state[i][j+2] == player && state[i][j+3] == player){
                    return true;
                }
            }
        }
        // ascendingDiagonalCheck
        for (int j=3; j<SIZE; j++){
            for (int i=0; i<SIZE-3; i++){
                if (state[i][j] == player && state[i+1][j-1] == player && state[i+2][j-2] == player && state[i+3][j-3] == player)
                    return true;
            }
        }
        // descendingDiagonalCheck
        for (int j=3; j<SIZE; j++){
            for (int i=3; i<SIZE; i++){
                if (state[i][j] == player && state[i-1][j-1] == player && state[i-2][j-2] == player && state[i-3][j-3] == player)
                    return true;
            }
        }
        return false;
    }

    private boolean checkPossibleMove() {
        int[][] state = this.getBoardState();

        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (state[y][x] == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getFreeCells() {
        int[][] state = this.getBoardState();
        int count = 0;

        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (state[y][x] == 0) {
                    count++;
                }
            }
        }

        return count;
    }

    public void doMove(int x, int y, int player) {
        if (!checkMove(x, y)) {
            throw new RuntimeException("Move not checked");
        }

        int index = y * SIZE + x;
        String playerSign = this.getPlayerSign(player);
        gridButtons[index].setText(playerSign);

        // Check if winning condition
        boolean win = this.checkWinningCondition(player);

        if (win) {
            this.endGame(player);
        } else if (!this.checkPossibleMove()) {
            this.endGame(0);
        }else {
            this.requestNextMove();
        }
    }

    protected void requestNextMove() {
        this.currentPlayer = (this.currentPlayer + 1) % 2;

        ConnectFourPlayer currentPlayer = this.getCurrentPlayer();

        int[][] state = this.getBoardState();
        currentPlayer.requestMove(state);
    }

    protected void endGame(int player) {
        for (JButton button : gridButtons) {
            button.setEnabled(false);
        }

        for (EndGameListener listener : endGameListeners) {
            listener.gameEnded(player);
        }
    }

    protected ConnectFourPlayer getWinner() {
        boolean winPlayer1 = this.checkWinningCondition(this.currentPlayer);
        if (winPlayer1) {
            return this.getCurrentPlayer();
        }
        int otherPlayer = (this.currentPlayer + 1) % 2;
        boolean winPlayer2 = this.checkWinningCondition(otherPlayer);
        if (winPlayer2) {
            return this.getPlayer(otherPlayer);
        }

        // The game is a draw, return null
        return null;
    }

    public boolean checkMove(int x, int y) {
        int[][] state = this.getBoardState();
        return state[y][x] == 0;
    }

    public int boardSize() {
        return this.SIZE;
    }

    private class MyActionListener implements ActionListener {

        private int index;

        public MyActionListener(int index) {
            this.index = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ConnectFourPlayer player = getCurrentPlayer();
            player.performMove(index);
        }
    }

    interface EndGameListener {
        void gameEnded(int winningPlayer);
    }

}
