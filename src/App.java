import javax.swing.*;

public class App {
    public static void main(String[] args) {
      // ウィンドウ変数
      int tileSize = 32;
      int rows = 16;
      int columns = 16;
      int boardWidth = tileSize * columns; // 32 * 16 = 512px
      int boardHeight = tileSize * rows;   // 32 * 16 = 512px

      JFrame frame = new JFrame("GALAXIAN");
      //frame.setVisible(true);
      frame.setSize(boardWidth, boardHeight);
      frame.setLocationRelativeTo(null);
      frame.setResizable(false);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      Galaxian galaxian = new Galaxian();
      frame.add(galaxian);
      frame.pack();
      galaxian.requestFocus();
      frame.setVisible(true);
  }
}