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

package com.siondream.superjumper.systems;

import appwarp.WarpController;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.siondream.superjumper.World;
import com.siondream.superjumper.components.BobComponent;
import com.siondream.superjumper.components.MovementComponent;
import com.siondream.superjumper.components.StateComponent;
import com.siondream.superjumper.components.TransformComponent;

public class BobSystem extends IteratingSystem {
	

	private static final Family family = Family.all(BobComponent.class,
													   StateComponent.class,
													   TransformComponent.class,
													   MovementComponent.class).get();
	protected float accelX = 0.0f;
	private World world;
	
	protected ComponentMapper<BobComponent> bm;
	protected ComponentMapper<StateComponent> sm;
	protected ComponentMapper<TransformComponent> tm;
	protected ComponentMapper<MovementComponent> mm;

	public float x;

	public float y;

	public int width;

	public int height;
	
	public BobSystem(World world) {
		super(family );
		
		this.world = world;
		
		bm = ComponentMapper.getFor(BobComponent.class);
		sm = ComponentMapper.getFor(StateComponent.class);
		tm = ComponentMapper.getFor(TransformComponent.class);
		mm = ComponentMapper.getFor(MovementComponent.class);
	}
	
	public void setAccelX(float accelX) {
		this.accelX = accelX;
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		accelX = 0.0f;
		
		sendLocation(x,y);
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		TransformComponent t = tm.get(entity);
		StateComponent state = sm.get(entity);
		MovementComponent mov = mm.get(entity);
		BobComponent bob = bm.get(entity);
		
		if (state.get() != BobComponent.STATE_HIT && t.pos.y <= 0.5f) {
			hitPlatform(entity);
		}
		
		if (state.get() != BobComponent.STATE_HIT) {
			mov.velocity.x = -accelX / 10.0f * BobComponent.MOVE_VELOCITY;
		}
		
		if (mov.velocity.y > 0 && state.get() != BobComponent.STATE_HIT) {
			if (state.get() != BobComponent.STATE_JUMP) {
				state.set(BobComponent.STATE_JUMP);
			}
		}

		if (mov.velocity.y < 0 && state.get() != BobComponent.STATE_HIT) {
			if (state.get() != BobComponent.STATE_FALL) {
				state.set(BobComponent.STATE_FALL);
			}
		}

		if (t.pos.x < 0) {
			t.pos.x = World.WORLD_WIDTH;
		}
		
		if (t.pos.x > World.WORLD_WIDTH) {
			t.pos.x = 0;
		}
		
		t.scale.x = mov.velocity.x < 0.0f ? Math.abs(t.scale.x) * -1.0f : Math.abs(t.scale.x);
		float side = mov.velocity.x < 0 ? -1 : 1;
		x = t.pos.x - 0.5f;
		y = t.pos.y - (0.5f * side);
		width =  1;
		height = 1;
		
		bob.heightSoFar = Math.max(t.pos.y, bob.heightSoFar);
		
		if (bob.heightSoFar - 7.5f > t.pos.y) {
			world.state = World.WORLD_STATE_GAME_OVER;
		}
	}
	
	public void hitSquirrel (Entity entity) {
		if (!family.matches(entity)) return;
		
		StateComponent state = sm.get(entity);
		MovementComponent mov = mm.get(entity);
		
		mov.velocity.set(0, 0);
		state.set(BobComponent.STATE_HIT);
	}

	public void hitPlatform (Entity entity) {
		if (!family.matches(entity)) return;
		
		StateComponent state = sm.get(entity);
		MovementComponent mov = mm.get(entity);
		
		mov.velocity.y = BobComponent.JUMP_VELOCITY;
		state.set(BobComponent.STATE_JUMP);
	}

	public void hitSpring (Entity entity) {
		if (!family.matches(entity)) return;
		
		StateComponent state = sm.get(entity);
		MovementComponent mov = mm.get(entity);
		
		mov.velocity.y = BobComponent.JUMP_VELOCITY * 1.5f;
		state.set(BobComponent.STATE_JUMP);
	}
		
	private void sendLocation(float x, float y) {
		try {
			Json data = new Json();
			String message = data.toJson(new Vector2(x, y));
			WarpController.getInstance().sendGameUpdate(message);

		} catch (Exception e) {
			// exception in sendLocation
		}
	}
}
