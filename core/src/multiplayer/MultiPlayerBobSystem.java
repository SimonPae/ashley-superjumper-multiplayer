/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package multiplayer;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.siondream.superjumper.components.BobComponent;
import com.siondream.superjumper.components.MovementComponent;
import com.siondream.superjumper.components.TransformComponent;

public class MultiPlayerBobSystem extends IteratingSystem {
	

	protected ComponentMapper<BobComponent> bm;
	protected ComponentMapper<TransformComponent> tm;

	public float x;

	public float y;

	public int width;

	public int height;


	private Json json;
	private Vector2 remotePosition;
	
	public MultiPlayerBobSystem() {
		super(Family.all(MultiPlayerBobComponent.class, TransformComponent.class).get());
		bm = ComponentMapper.getFor(BobComponent.class);
		tm = ComponentMapper.getFor(TransformComponent.class);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TransformComponent t = tm.get(entity);
		
		t.pos.x = x;
		t.pos.y = y;
	}

	public void onGameUpdateReceived (String message) {
		try {
			json = new Json();
			remotePosition = json.fromJson(Vector2.class, message);
			x = remotePosition.x;
			y = remotePosition.y;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
