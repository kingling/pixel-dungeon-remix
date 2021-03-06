package com.watabou.pixeldungeon.actors.blobs;

import com.nyrds.pixeldungeon.ml.R;
import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.effects.BlobEmitter;
import com.watabou.pixeldungeon.effects.particles.FlameParticle;
import com.watabou.pixeldungeon.levels.Terrain;
import com.watabou.pixeldungeon.scenes.GameScene;

public class LiquidFlame extends Blob {
	
	@Override
	protected void evolve() {

		boolean[] flammable = Dungeon.level.flammable;
		
		int from = getWidth() + 1;
		int to   = getLength() - getWidth() - 1;
		
		boolean observe = false;
		
		for (int pos=from; pos < to; pos++) {
			
			int fire = 0;
			
			if (cur[pos] > 0) {
				
				burn( pos );

				if(Dungeon.level.water[pos]) {
					cur[pos] = cur[pos]>1 ? cur[pos]/4 : 0;
 				} else {

					fire = cur[pos] - 1;
					if (fire <= 0 && flammable[pos]) {

						int oldTile = Dungeon.level.map[pos];
						Dungeon.level.set(pos, Terrain.EMBERS);

						observe = true;
						GameScene.updateMap(pos);
						if (Dungeon.visible[pos]) {
							GameScene.discoverTile(pos, oldTile);
						}
					}
				}
			} else {
				
				int fireInAdjCells = cur[pos-1] + cur[pos+1] + cur[pos-getWidth()] + cur[pos+getWidth()];
				
				if(fireInAdjCells > 0) {
					if(flammable[pos]) {
						fire = 4;
						burn( pos );
					} else if(!Dungeon.level.solid[pos] && !Dungeon.level.water[pos] && !Dungeon.level.pit[pos]) {
						fire = (fireInAdjCells-1) / 4;
						burn(pos);
					} else {
						fire = 0;
					}
				} else {
					fire = 0;
				}
			}
			
			volume += (off[pos] = fire);

		}
		
		if (observe) {
			Dungeon.observe();
		}
	}
	
	private void burn( int pos ) {
		Fire.burn(pos);
	}
	
	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
		emitter.start( FlameParticle.FACTORY, 0.03f, 0 );
	}
	
	@Override
	public String tileDesc() {
		return Game.getVar(R.string.Fire_Info);
	}
}
