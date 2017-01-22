package bg.fdiba.vgeorgieva;

public class HeroEntity extends Entity {
	private Object game;
	
	

	public HeroEntity(Object game, MySprite sprite, int x, int y) {
		super(sprite, x, y);
		this.game = game;
	}
	
	@Override
	public void collidedWith(Entity other) {
		Game myGame = (Game) game;
		myGame.notifyObjectCollision(this, other);
	}
}
