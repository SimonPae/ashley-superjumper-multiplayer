package multiplayer;

import appwarp.WarpController;
import appwarp.WarpListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.siondream.superjumper.Assets;
import com.siondream.superjumper.MainMenuScreen;
import com.siondream.superjumper.SuperJumper;

public class StartMultiPlayer implements Screen, WarpListener {
	SuperJumper game;

	OrthographicCamera guiCam;
	SpriteBatch batcher;
	Rectangle backBounds;
	Vector3 touchPoint;
	
	float xOffset = 0;
	
	private final String[] tryingToConnect = {"Connecting","to AppWarp"};
	private final String[] waitForOtherUser = {"Waiting for","other user"};
	private final String[] errorInConnection = {"Error in","Connection", "Go Back"};
	
	private final String[] game_win = {"Congrats You Win!", "Enemy Defeated"};
	private final String[] game_loose = {"Oops You Loose!","Target Achieved","By Enemy"};
	private final String[] enemy_left = {"Congrats You Win!", "Enemy Left the Game"};
	
	private String[] msg = tryingToConnect;

	public StartMultiPlayer (SuperJumper game) {
		this.game = game;

		guiCam = new OrthographicCamera(320, 480);
		guiCam.position.set(320 / 2, 480 / 2, 0);
		backBounds = new Rectangle(0, 0, 64, 64);
		touchPoint = new Vector3();
		batcher = new SpriteBatch();
		xOffset = 80;
		WarpController.getInstance().setListener(this);
	}

	public void update () {
		if (Gdx.input.justTouched()) {
			guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

			if (backBounds.contains(touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				game.setScreen(new MainMenuScreen(game));
				WarpController.getInstance().handleLeave();
				return;
			}
		}
	}

	public void draw () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		guiCam.update();

		batcher.setProjectionMatrix(guiCam.combined);
		batcher.disableBlending();
		batcher.begin();
		batcher.draw(Assets.backgroundRegion, 0, 0, 320, 480);
		batcher.end();

		batcher.enableBlending();
		batcher.begin();
		

		float y = 230;
		for (int i = msg.length-1; i >= 0; i--) {
			float width = Assets.font.getBounds(msg[i]).width;
			Assets.font.draw(batcher, msg[i], 160-width/2, y);
			y += Assets.font.getLineHeight();
		}

		batcher.draw(Assets.arrow, 0, 0, 64, 64);
		batcher.end();
	}

	@Override
	public void render (float delta) {
		update();
		draw();
	}

	@Override
	public void resize (int width, int height) {
	}

	@Override
	public void show () {
	}

	@Override
	public void hide () {
	}

	@Override
	public void pause () {
	}

	@Override
	public void resume () {
	}

	@Override
	public void dispose () {
	}
	
	@Override
	public void onError (String message) {
		this.msg = errorInConnection;
		update();
	}

	@Override
	public void onGameStarted (String message) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run () {
				game.setScreen(new MultiplayerScreen(game, StartMultiPlayer.this));
			}
		});
		
	}

	@Override
	public void onGameFinished (int code, boolean isRemote) {
		if(code==WarpController.GAME_WIN){
			this.msg = game_loose;
		}else if(code==WarpController.GAME_LOOSE){
			this.msg = game_win;
		}else if(code==WarpController.ENEMY_LEFT){
			this.msg = enemy_left;
		}
		update();
		game.setScreen(this);
	}
	
	@Override
	public void onGameUpdateReceived (String message) {
		
	}

	@Override
	public void onWaitingStarted(String message) {
		this.msg = waitForOtherUser;
		update();
	}

}