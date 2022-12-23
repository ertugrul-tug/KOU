import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;
import java.awt.Point;
import java.util.ArrayList;

public class XoxOyunu extends JFrame implements ActionListener {

  // Oyun tahtasının öğelerini tutan dizi
  private char[][] board;

  // Oyun tahtasını gösteren butonlar
  private JButton[][] buttons;

  // İlk hamle yapacak olan oyuncunun simgesi
  private char currentPlayer;

  // Bilgisayar hamlelerini yapacak olan rastgele sayı üreteci
  private Random random;

  // Hangi modda oynanacağını belirten değişken (kolay, zor veya minimax)
  private String mod;

  // Oyunun yeni başladığını belirten değişken
  private static boolean isNew = true;

  public XoxOyunu() {
    // Oyun tahtasını ve butonları oluştur
    board = new char[3][3];
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        board[i][j] = ' ';
      }
    }
    buttons = new JButton[3][3];

    // İlk oyuncu X olacak şekilde ayarla
    currentPlayer = 'X';

    // Bilgisayar hamlelerini yapacak olan rastgele sayı üreteci
    random = new Random();

    if (isNew){
      // Oyunun başlangıcında açıklama yap
      welcome();
      // Zorluk seçeneklerini açıkla
      descrption();
      isNew = false;
    }

    // Modu seç
    mod();

    // Oyun tahtasını oluştur
    JPanel panel = new JPanel(new GridLayout(3, 3));
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        // Buton oluştur ve oyun tahtasına ekle
        buttons[i][j] = new JButton();
        panel.add(buttons[i][j]);

        // Butona tıklama olayını dinle
        buttons[i][j].addActionListener(this);
      }
    }

    // İçeriği oluştur ve ekrana göster
    setContentPane(panel);
    setLocationRelativeTo(null);
    setTitle("XOX");
    setSize(200, 200);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setVisible(true);
  }

  public void actionPerformed(ActionEvent e) {
    // Tıklanan butonu bul
    JButton clickedButton = (JButton) e.getSource();
  
    // Tıklanan butonun koordinatlarını bul
    int row = -1;
    int col = -1;
    outerloop:
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (clickedButton == buttons[i][j]) {
          row = i;
          col = j;
          break outerloop;
        }
      }
    }
  
    // Tıklanan butona hamle yap
    if (board[row][col] == ' ') {
      board[row][col] = currentPlayer;
      clickedButton.setText(Character.toString(currentPlayer));
  
      // Oyunun bitip bitmediğini kontrol et
      if (checkWin()) {
        JOptionPane.showMessageDialog(this, currentPlayer + " kazandı!");
        resetGame();
      } else if (isDraw()) {
        JOptionPane.showMessageDialog(this, "Berabere!");
        resetGame();
      } else {
        // İleri oyuncunun hamlesini yap
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
        // Bilgisayarın hamlesini yap
        if (currentPlayer == 'O' && mod.equals("Kolay")) {makeRandomMove();}
        // Bilgisayarın hamlesini yap
          else if (currentPlayer == 'O' && mod.equals("Zor")) {makeStrategicMove();}
        // Bilgisayarın hamlesini yap
          else if (currentPlayer == 'O' && mod.equals("Minimax")) {makeMinimaxMove();}
        // Diğer oyuncu için karakteri değiştir
          else if (currentPlayer == 'O' && mod.equals("Düello")) {makeDuelloMove();}
      }
    }
  }

public void welcome(){
  // Oyunun başlangıcında açıklama yap
  String message = "XOX oyununa hoşgeldiniz.\n"
  + "Oyunun amacı, 3x3'lük bir tahtada, 3 tane X veya O'yu birleştirerek kazanmaktır.\n"
  + "Oyunu başlatmak için 'Ok' butonuna tıklayın.";
  JOptionPane.showMessageDialog(this, message, "XOX", JOptionPane.ERROR_MESSAGE);
}
public void descrption(){
  // Zorluk seçeneklerini açıkla
  String message = "Düello modda, iki oyuncu birbirleriyle oynar.\n"
  + "Kolay modda, bilgisayar sadece rastgele hamleler yapar.\n"
  + "Zor modda, bilgisayar en iyi hamleyi tahmin eder.\n"
  + "Minimax modda, bilgisayar en iyi hamleyi minimax algoritması ile bulur.";
  JOptionPane.showMessageDialog(this, message, "Zorluk seçenekleri", JOptionPane.INFORMATION_MESSAGE);
}

public void mod(){
// Modu seç
  Object[] options = {"Düello", "Kolay", "Zor", "Minimax"};
  mod = (String) JOptionPane.showInputDialog(this, "Mod seçin:", "XOX", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
  // Kullanıcı iptal ettiyse çık
  if (mod == null) { System.exit (0); }
}
// Oyun tahtasını sıfırlar ve ilk oyuncunun X olacak şekilde ayarlar
private void resetGame() {
  // Oyun tahtasını sıfırla
  setVisible(false);
  main(null);
}

// Bir oyuncunun kazanıp kazanmadığını kontrol eder
private boolean checkWin() {
  // Satırlar ve sütunlar üzerinde kontrol et
  for (int i = 0; i < 3; i++) {
    if (board[i][0] == currentPlayer && board[i][1] == currentPlayer && board[i][2] == currentPlayer) {
      return true;
    }
    if (board[0][i] == currentPlayer && board[1][i] == currentPlayer && board[2][i] == currentPlayer) {
      return true;
    }
  }

  // Çaprazlarda kontrol et
  if (board[0][0] == currentPlayer && board[1][1] == currentPlayer && board[2][2] == currentPlayer) {
    return true;
  }
  if (board[0][2] == currentPlayer && board[1][1] == currentPlayer && board[2][0] == currentPlayer) {
    return true;
  }

  // Hiçbir koşul sağlanmadıysa, kazanılamamıştır
  return false;
}

// Oyunun berabere sonuçlandığını kontrol eder
private boolean isDraw() {
  // Eğer tüm hücreler doluysa ve kazanan yoksa, berabere demektir
  for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
      if (board[i][j] == ' ') {
        // Hala boş hücreler var, berabere değil
        return false;
      }
    }
  }
  // Tüm hücreler dolu ve kazanan yok, berabere
  return true;
}

// Mevcut boş hücrelerin koordinatlarını tutan ArrayList'i döndürür
private ArrayList<Point> getAvailableMoves() {
  ArrayList<Point> emptyCells = new ArrayList<>();
  for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
      if (board[i][j] == ' ') {
        emptyCells.add(new Point(i, j));
      }
    }
  }
  return emptyCells;
}

private void makeDuelloMove() {
  // Oyuncunun simgesini değiştirir
  currentPlayer = 'O';
  if (checkWin()) {
    JOptionPane.showMessageDialog(this, currentPlayer + " kazandı!");
    resetGame();
  } else if (isDraw()) {
    JOptionPane.showMessageDialog(this, "Berabere!");
    resetGame();
  }
}

  private void makeRandomMove() {
    // Boş hücreleri bul
    currentPlayer = 'O';
    ArrayList<Point> emptyCells = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (board[i][j] == ' ') {
          emptyCells.add(new Point(i, j));
        }
      }
    }
  
    // Boş hücrelerden rastgele birini seç
    Point move = emptyCells.get(random.nextInt(emptyCells.size()));
    board[move.x][move.y] = 'O';
    buttons[move.x][move.y].setText("O");

    if (checkWin()) {
      JOptionPane.showMessageDialog(this, currentPlayer + " kazandı!");
      resetGame();
    } else if (isDraw()) {
      JOptionPane.showMessageDialog(this, "Berabere!");
      resetGame();
    }
    currentPlayer = 'X';
  }

  private void makeStrategicMove() {
    // İlk önce, kendi kazanabileceği hamleleri bul
    currentPlayer = 'O';
    ArrayList<Point> winningMoves = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (board[i][j] == ' ') {
          board[i][j] = 'O';
          if (checkWin()) {
            winningMoves.add(new Point(i, j));
          }
          board[i][j] = ' ';
        }
      }
    }
  
    // Eğer kendi kazanabileceği hamleler varsa, onlardan biri seç
    if (winningMoves.size() > 0) {
      Point move = winningMoves.get(random.nextInt(winningMoves.size()));
      board[move.x][move.y] = 'O';
      buttons[move.x][move.y].setText("O");
    } else {
      // İlk önce, rakibin kazanabileceği hamleleri bul
      ArrayList<Point> blockingMoves = new ArrayList<>();
      for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
          if (board[i][j] == ' ') {
            board[i][j] = 'X';
            if (checkWin()) {
              blockingMoves.add(new Point(i, j));
            }
            board[i][j] = ' ';
          }
        }
      }
  
      // Eğer rakibin kazanabileceği hamleler varsa, onları engelle
      if (blockingMoves.size() > 0) {
        Point move = blockingMoves.get(random.nextInt(blockingMoves.size()));
        board[move.x][move.y] = 'O';
        buttons[move.x][move.y].setText("O");
      } else {
        // Hiçbir işe yaramayan bir hamle yap
        makeRandomMove();
      }
    }

    if (checkWin()) {
      JOptionPane.showMessageDialog(this, currentPlayer + " kazandı!");
      resetGame();
    } else if (isDraw()) {
      JOptionPane.showMessageDialog(this, "Berabere!");
      resetGame();
    }
    currentPlayer = 'X';
  }

  private void makeMinimaxMove() {
    // Gelecek hamlelerin tüm olası sonuçlarını hesapla
    currentPlayer = 'O';
    ArrayList<Point> moves = getAvailableMoves();
    int[] scores = new int[moves.size()];
    for (int i = 0; i < scores.length; i++) {
      Point move = moves.get(i);
      board[move.x][move.y] = 'O';
      scores[i] = minimax(0, 'X');
      board[move.x][move.y] = ' ';
    }
  
    // En iyi skoru bul
    int maxScore = Integer.MIN_VALUE;
    int maxScoreIndex = -1;
    for (int i = 0; i < scores.length; i++) {
      if (scores[i] > maxScore) {
        maxScore = scores[i];
        maxScoreIndex = i;
      }
    }
  
    // En iyi hamleyi yap
    Point move = moves.get(maxScoreIndex);
    board[move.x][move.y] = 'O';
    buttons[move.x][move.y].setText("O");

    if (checkWin()) {
      JOptionPane.showMessageDialog(this, currentPlayer + " kazandı!");
      resetGame();
    } else if (isDraw()) {
      JOptionPane.showMessageDialog(this, "Berabere!");
      resetGame();
    }
    currentPlayer = 'X';
  }

  private int minimax(int depth, char player) {
    // Eğer oyun bitmişse, skoru döndür
    if (checkWin()) {
      return (player == 'O') ? 10 - depth : depth - 10;
    } else if (isDraw()) {
      return 0;
    }
  
    // Gelecek hamlelerin tüm olası sonuçlarını hesapla
    ArrayList<Point> moves = getAvailableMoves();
    int[] scores = new int[moves.size()];
    for (int i = 0; i < scores.length; i++) {
      Point move = moves.get(i);
      board[move.x][move.y] = player;
      if (player == 'O') {
        scores[i] = minimax(depth + 1, 'X');
      } else {
        scores[i] = minimax(depth + 1, 'O');
      }
      board[move.x][move.y] = ' ';
    }
  
    // En iyi skoru bul
    int maxScore = (player == 'O') ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    for (int score : scores) {
      if (player == 'O') {
        maxScore = Math.min(maxScore, score);
      } else {
        maxScore = Math.max(maxScore, score);
      }
    }
    return maxScore;
  }

  public static void main(String[] args) {
    XoxOyunu game = new XoxOyunu();
    game.setVisible(true);
  }
}
