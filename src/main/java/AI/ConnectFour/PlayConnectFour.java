package AI.ConnectFour;

import AI.ConnectFour.Player.ConnectFourPlayer;
import AI.ConnectFour.Player.HumanConnectFourPlayer;
import AI.ConnectFour.Player.RandomConnectFourPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayConnectFour extends JFrame {

    private static final int size = 7;

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

        gridButtons = new JButton[size*size];

        this.addEndGameListener(new EndGameListener() {
            @Override
            public void gameEnded(int winningPlayer) {
                System.out.println("Winning condition for player: " + winningPlayer);
            }
        });
    }

    public static void main(String[] args) {
        boolean headless = false;
        PlayConnectFour game = new PlayConnectFour(headless);
        ConnectFourPlayer random1 = new RandomConnectFourPlayer();
        ConnectFourPlayer random2 = new RandomConnectFourPlayer();
        game.setPlayer1(random1);
        game.setPlayer2(random2);



//        game.addEndGameListener(new EndGameListener() {
//            @Override
//            public void gameEnded(int winningPlayer) {
//
//            }
//        });

        game.renderGUI();
        game.start();
    }

    private void setPlayer1(ConnectFourPlayer player) {
        player1 = player;
        player1.setGame(this);
        player1.setPlayerId(1);
        player1.init();
    }

    private void setPlayer2(ConnectFourPlayer player) {
        player2 = player;
        player2.setGame(this);
        player2.setPlayerId(2);
        player2.init();
    }

    private void addEndGameListener(EndGameListener listener) {
        endGameListeners.add(listener);
    }

    private void renderGUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel game = new JPanel(new GridLayout(size, size));

        this.add(mainPanel);

        mainPanel.add(game, BorderLayout.SOUTH);

        for (int i = 0; i < size*size; i++) {

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
        if(currentPlayer == 0) {
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

    private void start() {
        ConnectFourPlayer currentPlayer = this.getCurrentPlayer();
        int[][] state = this.getBoardState();
        currentPlayer.requestMove(state);
    }

    private int[][] getBoardState() {
        int[][] state = new int[size][size];

        for (int i = 0; i < size*size; i++) {
            int x = i % size;
            int y = i / size;
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

        System.out.println(boardToString(state));

        // Source: https://stackoverflow.com/a/38211417

        // verticalCheck
        for (int i = 0; i<size-3 ; i++ ){
            for (int j = 0; j<size; j++){
                if (state[i][j] == player && state[i+1][j] == player && state[i+2][j] == player && state[i+3][j] == player){
                    return true;
                }
            }
        }
        // horizontalCheck
        for (int j = 0; j<size-3 ; j++ ){
            for (int i = 0; i<size; i++){
                if (state[i][j] == player && state[i][j+1] == player && state[i][j+2] == player && state[i][j+3] == player){
                    return true;
                }
            }
        }
        // ascendingDiagonalCheck
        for (int j=3; j<size; j++){
            for (int i=0; i<size-3; i++){
                if (state[i][j] == player && state[i+1][j-1] == player && state[i+2][j-2] == player && state[i+3][j-3] == player)
                    return true;
            }
        }
        // descendingDiagonalCheck
        for (int j=3; j<size; j++){
            for (int i=3; i<size; i++){
                if (state[i][j] == player && state[i-1][j-1] == player && state[i-2][j-2] == player && state[i-3][j-3] == player)
                    return true;
            }
        }
        return false;
    }

    public void doMove(int x, int y, int player) {
        if (!checkMove(x, y)) {
            throw new RuntimeException("Move not checked");
        }

        int index = y * size + x;
        String playerSign = this.getPlayerSign(player);
        gridButtons[index].setText(playerSign);

        // Check if winning condition
        boolean win = this.checkWinningCondition(player);

        if (win) {
            this.endGame(player);
        } else {
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

    public boolean checkMove(int x, int y) {
        int[][] state = this.getBoardState();
        return state[y][x] == 0;
    }

    public int boardSize() {
        return this.size;
    }

    private class MyActionListener implements ActionListener {

        private int index;

        public MyActionListener(int index) {
            this.index = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Placed on index: " + index);

            ConnectFourPlayer player = getCurrentPlayer();
            player.performMove(index);
        }
    }

    private abstract class EndGameListener {
        public abstract void gameEnded(int winningPlayer);
    }

}
