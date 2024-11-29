import javax.swing.*;

public class TicTacToeMejorado {
    private final Player[] players = new Player[10];
    private int playerCount = 0;
    private Player loggedInPlayer = null;

    public TicTacToeMejorado() {
        mainMenu();
    }

    private void mainMenu() {
        JFrame menuFrame = new JFrame("Menú Principal");
        menuFrame.setSize(300, 200);
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setLayout(new BoxLayout(menuFrame.getContentPane(), BoxLayout.Y_AXIS));

        JButton loginButton = new JButton("Iniciar Sesión");
        JButton registerButton = new JButton("Registro");
        JButton exitButton = new JButton("Salir");

        loginButton.addActionListener(e -> {
            menuFrame.dispose();
            login();
        });

        registerButton.addActionListener(e -> registerPlayer());

        exitButton.addActionListener(e -> System.exit(0));

        menuFrame.add(loginButton);
        menuFrame.add(registerButton);
        menuFrame.add(exitButton);

        menuFrame.setLocationRelativeTo(null);
        menuFrame.setVisible(true);
    }

    private void registerPlayer() {
        if (playerCount >= players.length) {
            JOptionPane.showMessageDialog(null, "El registro está lleno. No se pueden agregar más jugadores.");
            return;
        }

        String username = JOptionPane.showInputDialog("Ingrese un username único:");
        if (username == null || username.isEmpty()) return; // Cancelar registro
        if (findPlayerByUsername(username) != null) {
            JOptionPane.showMessageDialog(null, "El username ya está en uso. Intente con otro.");
            return;
        }

        String password;
        while (true) {
            password = JOptionPane.showInputDialog("Ingrese una contraseña de exactamente 5 caracteres:");
            if (password == null) return; // Cancelar registro
            if (password.length() == 5) break; // Contraseña válida
            JOptionPane.showMessageDialog(null, "La contraseña debe tener exactamente 5 caracteres. Intente nuevamente.");
        }

        players[playerCount++] = new Player(username, password, 0);
        JOptionPane.showMessageDialog(null, "Jugador registrado exitosamente. Ahora puede iniciar sesión.");
    }

    private void login() {
        String username = JOptionPane.showInputDialog("Ingrese su username:");
        if (username == null || username.isEmpty()) return;

        String password = JOptionPane.showInputDialog("Ingrese su contraseña:");
        if (password == null || password.isEmpty()) return;

        Player player = findPlayerByUsername(username);
        if (player != null && player.getPassword().equals(password)) {
            loggedInPlayer = player;
            JOptionPane.showMessageDialog(null, "Inicio de sesión exitoso.");
            mainGameMenu();
        } else {
            JOptionPane.showMessageDialog(null, "Username o contraseña incorrectos.");
        }
    }

    private void mainGameMenu() {
        JFrame menuFrame = new JFrame("Menú Principal");
        menuFrame.setSize(300, 200);
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setLayout(new BoxLayout(menuFrame.getContentPane(), BoxLayout.Y_AXIS));

        JButton playButton = new JButton("Jugar Tic Tac Toe");
        JButton rankingButton = new JButton("Ranking");
        JButton logoutButton = new JButton("Cerrar Sesión");

        playButton.addActionListener(e -> {
            menuFrame.dispose();
            startTicTacToeGame();
        });

        rankingButton.addActionListener(e -> showRanking());

        logoutButton.addActionListener(e -> {
            loggedInPlayer = null;
            JOptionPane.showMessageDialog(menuFrame, "Cierre de sesión exitoso.");
            menuFrame.dispose();
            mainMenu();
        });

        menuFrame.add(playButton);
        menuFrame.add(rankingButton);
        menuFrame.add(logoutButton);

        menuFrame.setLocationRelativeTo(null);
        menuFrame.setVisible(true);
    }

    private void startTicTacToeGame() {
        if (loggedInPlayer == null) {
            JOptionPane.showMessageDialog(null, "Debe iniciar sesión para jugar.");
            return;
        }

        String opponentUsername;
        Player opponentPlayer = null;

        while (true) {
            opponentUsername = JOptionPane.showInputDialog("Ingrese el username del oponente (o EXIT para cancelar):");
            if (opponentUsername == null || opponentUsername.equalsIgnoreCase("EXIT")) return;

            opponentPlayer = findPlayerByUsername(opponentUsername);
            if (opponentPlayer != null) break;

            JOptionPane.showMessageDialog(null, "Usuario no encontrado. Intente nuevamente.");
        }

        TicTacToe ticTacToe = new TicTacToe(loggedInPlayer, opponentPlayer);
    }

    private void showRanking() {
        if (playerCount == 0) {
            JOptionPane.showMessageDialog(null, "No hay jugadores registrados.");
            return;
        }

        StringBuilder ranking = new StringBuilder("Ranking de jugadores:\n");
        for (int i = 0; i < playerCount; i++) {
            ranking.append(i + 1)
                    .append(". ")
                    .append(players[i].getUsername())
                    .append(" - ")
                    .append(players[i].getPoints())
                    .append(" puntos\n");
        }
        JOptionPane.showMessageDialog(null, ranking.toString());
    }

    private Player findPlayerByUsername(String username) {
        for (int i = 0; i < playerCount; i++) {
            if (players[i].getUsername().equals(username)) {
                return players[i];
            }
        }
        return null;
    }

    public static void main(String[] args) {
        TicTacToeMejorado ticTacToeMejorado = new TicTacToeMejorado();
    }

    private static class TicTacToe {
        private JFrame frame;
        private final JButton[][] buttons;
        private final JLabel statusLabel;
        private boolean isXTurn = true;
        private final Player playerX;
        private final Player playerO;

        public TicTacToe(Player playerX, Player playerO) {
            this.playerX = playerX;
            this.playerO = playerO;

            frame = new JFrame("Tic Tac Toe");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(300, 400);
            frame.setLayout(null);

            statusLabel = new JLabel("Turno de " + playerX.getUsername() + " (X)", SwingConstants.CENTER);
            statusLabel.setBounds(10, 10, 260, 30);
            frame.add(statusLabel);

            buttons = new JButton[3][3];
            int size = 80;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    buttons[i][j] = new JButton("");
                    buttons[i][j].setBounds(10 + j * size, 50 + i * size, size, size);
                    buttons[i][j].setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
                    buttons[i][j].addActionListener(e -> handleButtonClick((JButton) e.getSource()));
                    frame.add(buttons[i][j]);
                }
            }

            JButton exitButton = new JButton("Salir");
            exitButton.setBounds(100, 310, 80, 30);
            exitButton.addActionListener(e -> frame.dispose());
            frame.add(exitButton);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }

        private void handleButtonClick(JButton button) {
            if (!button.getText().isEmpty()) return;

            button.setText(isXTurn ? "X" : "O");
            if (checkWinner()) {
                Player winner = isXTurn ? playerX : playerO;
                winner.addPoints(1);
                statusLabel.setText("¡Ganó " + winner.getUsername() + "!");
                disableButtons();
            } else if (isBoardFull()) {
                statusLabel.setText("¡Empate!");
            } else {
                isXTurn = !isXTurn;
                statusLabel.setText("Turno de " + (isXTurn ? playerX.getUsername() : playerO.getUsername()));
            }
        }

        private boolean checkWinner() {
            for (int i = 0; i < 3; i++) {
                if (buttons[i][0].getText().equals(buttons[i][1].getText()) &&
                    buttons[i][1].getText().equals(buttons[i][2].getText()) &&
                    !buttons[i][0].getText().isEmpty()) return true;
                if (buttons[0][i].getText().equals(buttons[1][i].getText()) &&
                    buttons[1][i].getText().equals(buttons[2][i].getText()) &&
                    !buttons[0][i].getText().isEmpty()) return true;
            }
            if (buttons[0][0].getText().equals(buttons[1][1].getText()) &&
                buttons[1][1].getText().equals(buttons[2][2].getText()) &&
                !buttons[0][0].getText().isEmpty()) return true;
            return buttons[0][2].getText().equals(buttons[1][1].getText()) &&
                    buttons[1][1].getText().equals(buttons[2][0].getText()) &&
                    !buttons[0][2].getText().isEmpty();
        }

        private boolean isBoardFull() {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().isEmpty()) return false;
            }
        }
        return true;
    }

    private void disableButtons() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
    }
    }

private static class Player {
    private final String username;
    private final String password;
    private int points;

    public Player(String username, String password, int points) {
        this.username = username;
        this.password = password;
        this.points = points;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int points) {
        this.points += points;
    }
}
}                            