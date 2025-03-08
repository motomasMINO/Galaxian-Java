import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.sound.sampled.Clip;

public class Galaxian extends JPanel implements ActionListener, KeyListener {
  class Block {
    int x;
    int y;
    int width;
    int height;
    Image img;
    boolean alive = true; //エイリアン用
    boolean used = false; //自機の弾丸用

    Block(int x, int y, int width, int height, Image img) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.img = img;
    }
  }
  //ボード
  int tileSize = 32;
  int rows = 16;
  int columns = 16;
  int boardWidth = tileSize * columns; // 32 * 16
  int boardHeight = tileSize * rows;

  // キャラ画像
  Image shipImg;
  Image alienRedImg;
  Image alienCyanImg;
  Image alienPinkImg;
  Image fragShipImg;
  ArrayList<Image> alienRedImgArray;
  ArrayList<Image> alienCyanImgArray;
  ArrayList<Image> alienPinkImgArray;
  ArrayList<Image> fragshipImgArray;

    // サウンド
    Clip backgroundMusic, startGame, Shoot, alienExplosion, fragshipExplosion, Loss, extraLife;

    // フォント
    Font scoreFont, livesFont, roundsFont;

  //自機
  int shipWidth = tileSize * 2; //64px
  int shipHeight = tileSize;    //32px
  int shipX = tileSize * columns / 2 - tileSize;
  int shipY = boardHeight - tileSize * 2;
  int shipVelocityX = tileSize; //自機の移動速度
  Block ship;

  //赤エイリアン
  ArrayList<Block> alienRedArray;
  int alienRedWidth = tileSize * 2;
  int alienRedHeight = tileSize;
  int alienRedX = tileSize;
  int alienRedY = tileSize * 2;

  int alienRedRows = 1;
  int alienRedColumns = 5;
  int alienRedCount = 0;  //倒さなきゃいけない赤エイリアンの数
  int alienRedVelocityX = 1; //赤エイリアンの移動速度
  Block alienRed;

  //シアンエイリアン
  ArrayList<Block> alienCyanArray;
  int alienCyanWidth = tileSize * 2;
  int alienCyanHeight = tileSize;
  int alienCyanX = tileSize;
  int alienCyanY = tileSize * 4;

  int alienCyanRows = 1;
  int alienCyanColumns = 5;
  int alienCyanCount = 0;  //倒さなきゃいけないシアンエイリアンの数
  int alienCyanVelocityX = 1; //シアンエイリアンの移動速度
  Block alienCyan;

  //ピンクエイリアン
  ArrayList<Block> alienPinkArray;
  int alienPinkWidth = tileSize * 2;
  int alienPinkHeight = tileSize;
  int alienPinkX = tileSize;
  int alienPinkY = tileSize * 3;

  int alienPinkRows = 1;
  int alienPinkColumns = 5;
  int alienPinkCount = 0;  //倒さなきゃいけないピンクエイリアンの数
  int alienPinkVelocityX = 1; //ピンクエイリアンの移動速度
  Block alienPink;

  //旗艦
  ArrayList<Block> fragshipArray;
  int fragshipWidth = tileSize * 2;
  int fragshipHeight = tileSize;
  int fragshipX = tileSize;
  int fragshipY = tileSize;

  int fragshipRows = 1;
  int fragshipColumns = 5;
  int fragshipCount = 0;  //倒さなきゃいけない旗艦の数
  int fragshipVelocityX = 1; //旗艦の移動速度
  Block fragship;


  //自機の弾丸
  ArrayList<Block> bulletArray;
  int bulletWidth = tileSize / 8;
  int bulletHeight = tileSize / 2;
  int bulletVelocityY = -15; //弾丸の移動速度

  //敵の弾丸
  ArrayList<Block> enemyBulletArray;
  int enemyBulletWidth = tileSize / 8;
  int enemyBulletHeight = tileSize / 2;
  int enemyBulletVelocityY = 7; //弾丸の移動速度

  Timer gameLoop;
  int score = 0; // スコア
  int lives = 3; // 残機
  int rounds = 1; // ラウンド
  int bonusScoreThreshold = 10000; // ボーナススコアの初期値
  boolean gameOver = false; // ゲームオーバー用
  
  Galaxian() {
    setPreferredSize(new Dimension(boardWidth, boardHeight));
    setBackground(Color.black);
    setFocusable(true);
    addKeyListener(this);

    //画像を読み込む
    shipImg = new ImageIcon(getClass().getResource("./Galacship.png")).getImage();
    alienRedImg = new ImageIcon(getClass().getResource("./RedAlien.jpg")).getImage();
    alienCyanImg = new ImageIcon(getClass().getResource("./CyanAlien.jpg")).getImage();
    alienPinkImg = new ImageIcon(getClass().getResource("./PinkAlien.jpg")).getImage();
    fragShipImg = new ImageIcon(getClass().getResource("./Fragship.png")).getImage();

    alienRedImgArray = new ArrayList<Image>();
    alienCyanImgArray = new ArrayList<Image>();
    alienPinkImgArray = new ArrayList<Image>();
    fragshipImgArray = new ArrayList<Image>();
    alienRedImgArray.add(alienRedImg);
    alienCyanImgArray.add(alienCyanImg);
    alienPinkImgArray.add(alienPinkImg);
    fragshipImgArray.add(fragShipImg);

    ship = new Block(shipX, shipY, shipWidth, shipHeight, shipImg);
    alienRedArray = new ArrayList<Block>();
    alienCyanArray = new ArrayList<Block>();
    alienPinkArray = new ArrayList<Block>();
    fragshipArray = new ArrayList<Block>();
    bulletArray = new ArrayList<Block>();
    enemyBulletArray = new ArrayList<Block>();

    // サウンド読み込み
    Shoot = Loader.loadSound("./Shoot.wav");
    alienExplosion = Loader.loadSound("./HitEnemy.wav");
    fragshipExplosion = Loader.loadSound("./HitBoss.wav");
    backgroundMusic = Loader.loadSound("./backgroundMusic.wav");
    startGame = Loader.loadSound("./StartGame.wav");
    Loss =Loader.loadSound("./FighterLoss.wav");
    extraLife = Loader.loadSound("./Extra-Life.wav");

    // フォント読み込み
    scoreFont = Loader.loadFont("./arcadeFont.ttf", 30);
    livesFont = Loader.loadFont("./arcadeFont.ttf", 20);
    roundsFont = Loader.loadFont("./arcadeFont.ttf", 20);

    //ゲームタイマー
    gameLoop = new Timer(1000 / 60, this); //1000 / 60 = 16.7
    createRedAliens();
    createCyanAliens();
    createFragship();
    createPinkAliens();
    gameLoop.start();
    startGame.start();
    backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    draw(g);
  }

  public void draw(Graphics g) {
    //自機
    g.drawImage(ship.img, ship.x, ship.y, ship.width, ship.height, null);

    //赤エイリアン
    for(int i = 0; i < alienRedArray.size(); i++) {
      Block alienRed = alienRedArray.get(i);
      if(alienRed.alive) {
        g.drawImage(alienRed.img, alienRed.x, alienRed.y, alienRed.width, alienRed.height, null);
      }
    }

    //シアンエイリアン
    for(int i = 0; i < alienCyanArray.size(); i++) {
      Block alienCyan = alienCyanArray.get(i);
      if(alienCyan.alive) {
        g.drawImage(alienCyan.img, alienCyan.x, alienCyan.y, alienCyan.width, alienCyan.height, null);
      }
    }

    //ピンクエイリアン
    for(int i = 0; i < alienPinkArray.size(); i++) {
      Block alienPink = alienPinkArray.get(i);
      if(alienPink.alive) {
        g.drawImage(alienPink.img, alienPink.x, alienPink.y, alienPink.width, alienPink.height, null);
      }
    }

    //旗艦
    for(int i = 0; i < fragshipArray.size(); i++) {
      Block fragship = fragshipArray.get(i);
      if(fragship.alive) {
        g.drawImage(fragship.img, fragship.x, fragship.y, fragship.width, fragship.height, null);
      }
    }

    //自機の弾丸
    g.setColor(Color.yellow);
    for(int i = 0; i < bulletArray.size(); i++) {
       Block bullet = bulletArray.get(i);
       if(!bullet.used) {
         g.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
       }
    }

    // 敵の弾丸
    g.setColor(Color.white);
    for(int i = 0; i < enemyBulletArray.size(); i++) {
       Block enemyBullet = enemyBulletArray.get(i);
       g.fillRect(enemyBullet.x, enemyBullet.y, enemyBullet.width, enemyBullet.height);
    }

    //スコア
    g.setFont(scoreFont);
    if(gameOver) {
      g.setColor(Color.red);
      g.drawString("GAME OVER: " + String.valueOf(score), 10, 35);
    }
    else {
        g.setColor(Color.white);
        g.drawString("SCORE: " + String.valueOf(score), 10, 35);
    }

    //残機
    g.setColor(Color.white);
    g.setFont(livesFont);
    g.drawString("LIFE x " + String.valueOf(lives), 10, 500);

    // ラウンド
    g.setColor(Color.white);
    g.setFont(roundsFont);
    g.drawString("ROUND " + String.valueOf(rounds), 350, 500);
  } 

  public void move() {
    Random random = new Random();

    //赤エイリアン
    for(int i = 0; i < alienRedArray.size(); i++) {
       Block alienRed = alienRedArray.get(i);
       if(alienRed.alive) {
         alienRed.x += alienRedVelocityX;
      
         //赤エイリアンが境界に触れたときの動作
         if(alienRed.x + alienRed.width >= boardWidth || alienRed.x <= 0) {
           alienRedVelocityX *=-1;
           alienRed.x += alienRedVelocityX *2;
         }
       }
    }

    //シアンエイリアン
    for(int i = 0; i < alienCyanArray.size(); i++) {
      Block alienCyan = alienCyanArray.get(i);
      if(alienCyan.alive) {
        alienCyan.x += alienCyanVelocityX;
     
        //シアンエイリアンが境界に触れたときの動作
        if(alienCyan.x + alienCyan.width >= boardWidth || alienCyan.x <= 0) {
          alienCyanVelocityX *=-1;
          alienCyan.x += alienCyanVelocityX *2;
        }
      }
   }

   //ピンクエイリアン
   for(int i = 0; i < alienPinkArray.size(); i++) {
    Block alienPink = alienPinkArray.get(i);
    if(alienPink.alive) {
      alienPink.x += alienPinkVelocityX;
   
      //ピンクエイリアンが境界に触れたときの動作
      if(alienPink.x + alienPink.width >= boardWidth || alienPink.x <= 0) {
        alienPinkVelocityX *=-1;
        alienPink.x += alienPinkVelocityX *2;
      }
    }
 }

   //旗艦
   for(int i = 0; i < fragshipArray.size(); i++) {
    Block fragship = fragshipArray.get(i);
    if(fragship.alive) {
      fragship.x += fragshipVelocityX;
   
      //旗艦が境界に触れたときの動作
      if(fragship.x + fragship.width >= boardWidth || fragship.x <= 0) {
        fragshipVelocityX *=-1;
        fragship.x += fragshipVelocityX *2;
      }


    }
 }

// 赤エイリアンの弾丸発射
for (Block alienRed : alienRedArray) {
  if (alienRed.alive && random.nextInt(1000) < 5) { // 0.5% の確率で発射
      Block enemyBullet = new Block(alienRed.x + alienRed.width / 2, alienRed.y + alienRed.height, enemyBulletWidth, enemyBulletHeight, null);
      enemyBulletArray.add(enemyBullet);
  }
}

// シアンエイリアンの弾丸発射
for (Block alienCyan : alienCyanArray) {
  if (alienCyan.alive && random.nextInt(1000) < 5) {
      Block enemyBullet = new Block(alienCyan.x + alienCyan.width / 2, alienCyan.y + alienCyan.height, enemyBulletWidth, enemyBulletHeight, null);
      enemyBulletArray.add(enemyBullet);
  }
}

// ピンクエイリアンの弾丸発射
for (Block alienPink : alienPinkArray) {
  if (alienPink.alive && random.nextInt(1000) < 5) {
      Block enemyBullet = new Block(alienPink.x + alienPink.width / 2, alienPink.y + alienPink.height, enemyBulletWidth, enemyBulletHeight, null);
      enemyBulletArray.add(enemyBullet);
  }
}

// 旗艦の弾丸発射
for (Block fragship : fragshipArray) {
  if (fragship.alive && random.nextInt(1000) < 5) {
      Block enemyBullet = new Block(fragship.x + fragship.width / 2, fragship.y + fragship.height, enemyBulletWidth, enemyBulletHeight, null);
      enemyBulletArray.add(enemyBullet);
  }
}

// 敵の弾丸の移動
for (int i = 0; i < enemyBulletArray.size(); i++) {
  Block enemyBullet = enemyBulletArray.get(i);
  enemyBullet.y += enemyBulletVelocityY;

  // 敵の弾丸と自機の当たり判定
  if (detectCollision(enemyBullet, ship)) {
    lives--; // 残機-1
    enemyBulletArray.clear(); // 敵弾丸を消す
    bulletArray.clear(); // 自機の弾丸を消す
    ship.x = shipX; // 自機を初期位置に戻す
    Loss.setFramePosition(0);
    Loss.start();
    if(lives == 0) { // 残機が0になるとゲームオーバー
      gameOver = true;
      enemyBulletArray.clear();
      bulletArray.clear();
      ship.y = -35; // 自機を画面外に出す
      break;
    }
  }
}

// 画面外に出た敵の弾丸を削除
while (enemyBulletArray.size() > 0 && enemyBulletArray.get(0).y > boardHeight) {
  enemyBulletArray.remove(0);
}

    //自機の弾丸
    for(int i = 0; i < bulletArray.size(); i++) {
      Block bullet = bulletArray.get(i);
      bullet.y += bulletVelocityY;

    //弾丸と赤エイリアンの当たり判定
    for(int j = 0; j < alienRedArray.size(); j++) {
       Block alienRed = alienRedArray.get(j);
       if(!bullet.used && alienRed.alive && detectCollision(bullet, alienRed)) {
         bullet.used = true;
         alienRed.alive = false;
         alienRedCount--;
         score += alienRedColumns * alienRedRows * 10;
         alienExplosion.setFramePosition(0);
         alienExplosion.start();
       }
      }
    
    //弾丸とシアンエイリアンの当たり判定
    for(int j = 0; j < alienCyanArray.size(); j++) {
      Block alienCyan = alienCyanArray.get(j);
      if(!bullet.used && alienCyan.alive && detectCollision(bullet, alienCyan)) {
        bullet.used = true;
        alienCyan.alive = false;
        alienCyanCount--;
        score += alienCyanColumns * alienCyanRows * 6;
        alienExplosion.setFramePosition(0);
        alienExplosion.start();
      }
     }
     
    //弾丸とピンクエイリアンの当たり判定
    for(int j = 0; j < alienPinkArray.size(); j++) {
      Block alienPink = alienPinkArray.get(j);
      if(!bullet.used && alienPink.alive && detectCollision(bullet, alienPink)) {
        bullet.used = true;
        alienPink.alive = false;
        alienPinkCount--;
        score += alienPinkColumns * alienPinkRows * 8;
        alienExplosion.setFramePosition(0);
        alienExplosion.start();
      }
     } 

    //弾丸と旗艦の当たり判定
    for(int j = 0; j < fragshipArray.size(); j++) {
      Block fragship = fragshipArray.get(j);
      if(!bullet.used && fragship.alive && detectCollision(bullet, fragship)) {
        bullet.used = true;
        fragship.alive = false;
        fragshipCount--;
        score += fragshipColumns * fragshipRows * 12;
        fragshipExplosion.setFramePosition(0);
        fragshipExplosion.start();
      }
     }
    }

    //自機の弾丸をクリア
    while(bulletArray.size() > 0 && (bulletArray.get(0).used || bulletArray.get(0).y < 0)) {
      bulletArray.remove(0); //配列の最初の要素を削除
    }

    // スコアがボーナススコア閾値を超えたら残機を追加
    if(score >= bonusScoreThreshold) {
      lives++; // 残機を増やす
      bonusScoreThreshold += 10000; // 次のボーナススコア閾値を設定 
      extraLife.setFramePosition(0);
      extraLife.start();
    }

    //敵をすべて倒したら、次のラウンドへ進む
    if(fragshipCount == 0 && alienRedCount == 0 && alienCyanCount == 0 && alienPinkCount == 0) {
      //敵の数を戻す
      fragshipColumns = Math.min(fragshipColumns, columns / 2);
      fragshipRows = Math.min(fragshipRows, rows);
      alienRedColumns = Math.min(alienRedColumns, columns / 2);
      alienRedRows = Math.min(alienRedRows, rows);
      alienCyanColumns = Math.min(alienCyanColumns, columns / 2);
      alienCyanRows = Math.min(alienCyanRows, rows);
      alienPinkColumns = Math.min(alienPinkColumns, columns / 2);
      alienPinkRows = Math.min(alienPinkRows, rows);
      fragshipArray.clear();
      alienRedArray.clear();
      alienCyanArray.clear();
      alienPinkArray.clear();
      bulletArray.clear(); // 自機の弾丸を消す
      enemyBulletArray.clear(); // 敵の弾丸を消す
      fragshipVelocityX = 1;
      alienRedVelocityX = 1;
      alienCyanVelocityX = 1;
      alienPinkVelocityX = 1;
      createFragship();     
      createRedAliens();
      createCyanAliens();
      createPinkAliens();
      rounds++; // ラウンド数+1
    }
  }

  public void createRedAliens() {
    Random random = new Random();
    for(int r = 0; r < alienRedRows; r++){
      for(int c = 0; c < alienRedColumns; c++) {
        int randomImgIndex = random.nextInt(alienRedImgArray.size());
         Block alienRed = new Block(
          alienRedX + c * alienRedWidth,
          alienRedY + r * alienRedHeight,
          alienRedWidth,
          alienRedHeight,
          alienRedImgArray.get(randomImgIndex)
         );
         alienRedArray.add(alienRed);
      }
    }
    alienRedCount = alienRedArray.size();
  }

  public void createCyanAliens() {
    Random random = new Random();
    for(int r = 0; r < alienCyanRows; r++){
      for(int c = 0; c < alienCyanColumns; c++) {
        int randomImgIndex = random.nextInt(alienCyanImgArray.size());
         Block alienCyan = new Block(
          alienCyanX + c * alienCyanWidth,
          alienCyanY + r * alienCyanHeight,
          alienCyanWidth,
          alienCyanHeight,
          alienCyanImgArray.get(randomImgIndex)
         );
         alienCyanArray.add(alienCyan);
      }
    }
    alienCyanCount = alienCyanArray.size();
  }

  public void createPinkAliens() {
    Random random = new Random();
    for(int r = 0; r < alienPinkRows; r++){
      for(int c = 0; c < alienPinkColumns; c++) {
        int randomImgIndex = random.nextInt(alienPinkImgArray.size());
         Block alienPink = new Block(
          alienPinkX + c * alienPinkWidth,
          alienPinkY + r * alienPinkHeight,
          alienPinkWidth,
          alienPinkHeight,
          alienPinkImgArray.get(randomImgIndex)
         );
         alienPinkArray.add(alienPink);
      }
    }
    alienPinkCount = alienPinkArray.size();
  }

  public void createFragship() {
    Random random = new Random();
    for(int r = 0; r < fragshipRows; r++){
      for(int c = 0; c < fragshipColumns; c++) {
        int randomImgIndex = random.nextInt(fragshipImgArray.size());
         Block fragship = new Block(
          fragshipX + c * fragshipWidth,
          fragshipY + r * fragshipHeight,
          fragshipWidth,
          fragshipHeight,
          fragshipImgArray.get(randomImgIndex)
         );
         fragshipArray.add(fragship);
      }
    }
    fragshipCount = fragshipArray.size();
  }

  public boolean detectCollision(Block a, Block b) {
    return a.x < b.x + b.width &&  //aの左上隅がbの右上隅に届かない
           a.x + a.width > b.x &&  //aの右上隅がbの左上隅を通過する
           a.y < b.y + b.height && //aの左上隅がbの左下隅に届かない
           a.y + a.height > b.y;   //aの左下隅がbの左上隅を通過する
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
    move();
    repaint();
    if(gameOver) {
      gameLoop.stop();
      backgroundMusic.stop();
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {}

  @Override
  public void keyPressed(KeyEvent e) {}

  @Override
  public void keyReleased(KeyEvent e) {
    if(gameOver) { //リセットするには何かキーを押してください
      ship.x = shipX; // 自機を初期位置に戻す
      ship.y = shipY; // 自機を再び出現させる
      fragshipArray.clear();
      alienRedArray.clear();
      alienCyanArray.clear();
      alienPinkArray.clear();
      bulletArray.clear();
      score = 0;
      lives = 3;
      rounds = 1;
      bonusScoreThreshold = 10000; // ボーナススコアを初期化
      fragshipVelocityX = 1;
      fragshipColumns = 5;
      fragshipRows = 1;
      alienRedVelocityX = 1;
      alienRedColumns = 5;
      alienRedRows = 1;
      alienCyanVelocityX = 1;
      alienCyanColumns = 5;
      alienCyanRows = 1;
      alienPinkVelocityX = 1;
      alienPinkColumns = 5;
      alienPinkRows = 1;
      gameOver = false;
      createFragship();
      createRedAliens();
      createCyanAliens();
      createPinkAliens();
      gameLoop.start();
      startGame.setFramePosition(0);
      startGame.start();
      backgroundMusic.setFramePosition(0);
      backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
    }
    else if(e.getKeyCode() == KeyEvent.VK_LEFT && ship.x - shipVelocityX >= 0) {
       ship.x -= shipVelocityX + 1;  //1タイル左へ移動
    }
    else if(e.getKeyCode() == KeyEvent.VK_RIGHT && ship.x + ship.width + shipVelocityX <= boardWidth) {
        ship.x += shipVelocityX + 1; //1タイル右へ移動
    }
    else if(e.getKeyCode() == KeyEvent.VK_SPACE) {
      Block bullet = new Block(ship.x + shipWidth * 15 / 32, ship.y, bulletWidth, bulletHeight, null);
      bulletArray.add(bullet); // スペースキーで弾丸発射(連射可能)
      Shoot.setFramePosition(0);
      Shoot.start();
    }
   }
  }