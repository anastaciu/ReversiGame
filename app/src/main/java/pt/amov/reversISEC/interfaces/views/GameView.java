package pt.amov.reversISEC.interfaces.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import java.util.List;

import pt.amov.reversISEC.logic.Play;
import pt.amov.reversISEC.logic.Constants;
import pt.amov.reversISEC.R;


public class GameView extends SurfaceView implements Callback, Constants{

	private BoardUpdate thread;
	private float boardSize;
	private float squareSize;
	private float boardLeft;
	private float boardRight;
	private float boardTop;
	private float boardBottom;
	private byte[][] gameBoard;
	private int[][] index;
	private Bitmap[] images;
	private Bitmap background;



	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		float screenWidth, ratio = 0.9f, scale[] = new float[] {0.9f, 0.9f, 0.9f, 0.9f, 0.9f}, margin;
		int scaleLevel = 2;
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GameView);
		typedArray.getFloat(R.styleable.GameView_ratio, ratio);
		typedArray.recycle();

		getHolder().addCallback(this);

		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		boardSize = screenWidth * scale[scaleLevel];
		squareSize = (8f / 9f * boardSize) / 8;
		margin = 1f / 18f * boardSize;
		boardLeft = margin;
		boardRight = boardLeft + BOARD_SIZE * squareSize;
		boardTop = margin;
		boardBottom = boardTop + BOARD_SIZE * squareSize;
		images = new Bitmap[TOKENS];
		loadTokens(context);
		background = loadImages(boardSize, boardSize, context.getDrawable(R.drawable.board));
		initGameBoard();
	}


	public void initGameBoard(){
		gameBoard = new byte[BOARD_SIZE][BOARD_SIZE];
		index = new int[BOARD_SIZE][BOARD_SIZE];

		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				gameBoard[i][j] = NULL;
			}
		}
		gameBoard[3][3] = WHITE;
		gameBoard[3][4] = BLACK;
		gameBoard[4][3] = BLACK;
		gameBoard[4][4] = WHITE;

		index[3][3] = TOKENS/2;
		index[3][4] = 0;
		index[4][3] = 0;
		index[4][4] = TOKENS;
	}


	private int updateIndex(int index, int color) {

		if (index == 0 || index == 11) {
			return index;
		} else if (index >= 1 && index <= 10 || index >= 12 && index <= 21) {
			return (index + 1) % 22;
		} else {
			return defaultIndex(color);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) boardSize, View.MeasureSpec.EXACTLY);
		heightMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) boardSize, View.MeasureSpec.EXACTLY);
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void play(byte[][] gameBoard, List<Play> reversed, Play play) {

		for (int i = 0; i < BOARD_SIZE; i++)
			System.arraycopy(gameBoard[i], 0, this.gameBoard[i], 0, BOARD_SIZE);
		for (int i = 0; i < reversed.size(); i++) {
			int reverseRow = reversed.get(i).line;
			int reverseCol = reversed.get(i).col;
			if (gameBoard[reverseRow][reverseCol] == WHITE) {
				index[reverseRow][reverseCol] = 1;
			} else if (gameBoard[reverseRow][reverseCol] == BLACK) {
				index[reverseRow][reverseCol] = 12;
			}
		}
		int line = play.line, col = play.col;
		if (gameBoard[line][col] == WHITE) {
			index[line][col] = 11;
		} else if (gameBoard[line][col] == BLACK) {
			index[line][col] = 0;
		}

	}

	public void update() {

		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				if (gameBoard[i][j] == NULL)
					continue;
				index[i][j] = updateIndex(index[i][j], gameBoard[i][j]);
			}
		}

	}

	public boolean inGameBoard(float x, float y) {
		return !(x >= boardLeft) || !(x <= boardRight) || !(y >= boardTop) || !(y <= boardBottom);
	}

	public int getLine(float y) {
        return (int) Math.floor((y - boardTop) / squareSize);
	}

	public int getCol(float x) {
		return (int) Math.floor((x - boardLeft) / squareSize);

    }

	public void render(Canvas canvas) {
		Paint board = new Paint();
		Paint tokens = new Paint();
		canvas.drawBitmap(background, 0, 6, board);

		for (int col = 0; col < BOARD_SIZE; col++) {
			for (int line = 0; line < BOARD_SIZE; line++) {
				if (gameBoard[line][col] != NULL) {
					canvas.drawBitmap(images[index[line][col]], boardLeft + col * squareSize, boardTop + line * squareSize, tokens);
				}
			}
		}
	}

	public int defaultIndex(int color) {
		if (color == WHITE)
			return 11;
		else if (color == BLACK)
			return 0;
		return -1;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		thread = new BoardUpdate(getHolder(), this);
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		thread.setRunning(false);
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Bitmap loadImages(float width, float height, Drawable drawable) {

		Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, (int)width, (int)height);
		drawable.draw(canvas);
		return bitmap;
	}

	private void loadTokens(Context context) {

		images[0] = loadImages(squareSize, squareSize, context.getDrawable(R.drawable.black_1));
		images[1] = loadImages(squareSize, squareSize, context.getDrawable(R.drawable.black_2));
		images[2] = loadImages(squareSize, squareSize, context.getDrawable(R.drawable.black_3));
		images[3] = loadImages(squareSize, squareSize, context.getDrawable(R.drawable.black_4));
		images[4] = loadImages(squareSize, squareSize, context.getDrawable(R.drawable.black_5));
		images[5] = loadImages(squareSize, squareSize, context.getDrawable(R.drawable.black_6));
		images[6] = loadImages(squareSize, squareSize, context.getDrawable(R.drawable.black_7));
		images[7] = loadImages(squareSize, squareSize, context.getDrawable(R.drawable.black_8));
		images[8] = loadImages(squareSize, squareSize, context.getDrawable(R.drawable.black_9));
		images[9] = loadImages(squareSize, squareSize, context.getDrawable(R.drawable.black_10));
		images[10] = loadImages(squareSize, squareSize, context.getDrawable(R.drawable.black_11));
		images[11] = loadImages(squareSize, squareSize, context.getDrawable(R.drawable.white_1));
		images[12] = loadImages(squareSize, squareSize, context.getDrawable(R.drawable.white_2));
		images[13] = loadImages(squareSize, squareSize, context.getDrawable(R.drawable.white_3));
		images[14] = loadImages(squareSize, squareSize, context.getDrawable(R.drawable.white_4));
		images[15] = loadImages(squareSize, squareSize, context.getDrawable(R.drawable.white_5));
		images[16] = loadImages(squareSize, squareSize, context.getDrawable(R.drawable.white_6));
		images[17] = loadImages(squareSize, squareSize, context.getDrawable(R.drawable.white_7));
		images[18] = loadImages(squareSize, squareSize, context.getDrawable(R.drawable.white_8));
		images[19] = loadImages(squareSize, squareSize, context.getDrawable(R.drawable.white_9));
		images[20] = loadImages(squareSize, squareSize, context.getDrawable(R.drawable.white_10));
		images[21] = loadImages(squareSize, squareSize, context.getDrawable(R.drawable.white_11));
	}

}
