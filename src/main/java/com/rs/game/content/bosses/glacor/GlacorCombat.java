// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content.bosses.glacor;

import com.rs.game.World;
import com.rs.game.content.Effect;
import com.rs.game.content.bosses.glacor.Glacor.InheritedType;
import com.rs.game.content.combat.CombatStyle;
import com.rs.game.model.WorldProjectile;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.utils.Ticks;
import kotlin.Pair;

public class GlacorCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 14301 };
	}

	final int MAGE_PROJECTILE = 634;
	final int RANGE_PROJECTILE = 962;
	final int EXPLODE_GFX = 739;
	final int SPECIAL_PROJECTILE = 2314;

	int attackType = 0;

	@Override
	public int attack(NPC npc, final Entity target) {
		if (target instanceof NPC) {
			npc.sync(9968, 905);
			WorldProjectile p = World.sendProjectile(npc, target, SPECIAL_PROJECTILE, new Pair<>(60, 32), 50, 5, 0);
			final Tile targetPosition = Tile.of(target.getX(), target.getY(), target.getPlane());
			WorldTasks.schedule(new Task() {
				@Override
				public void run() {
					if ((target.getX() == targetPosition.getX()) && (target.getY() == targetPosition.getY()))
						target.applyHit(new Hit(target, 500, HitLook.TRUE_DAMAGE));
					World.sendSpotAnim(targetPosition, new SpotAnim(2315));
				}
			}, p.getTaskDelay());
			return 0;
		}
		if (target instanceof Player player) {
			final Glacor glacor = (Glacor) npc;

			glacor.lastAttacked = player;

			attackType = Utils.random(1, 3);

			if (glacor.getMinionType() != null && glacor.getMinionType() == InheritedType.SAPPING)
				player.getPrayer().drainPrayer(50);

			if (Utils.random(100) < 10)
				attackType = 3;

			if (attackType == 1) {
				npc.sync(9967, 902);
				WorldProjectile p = World.sendProjectile(npc, target, MAGE_PROJECTILE, new Pair<>(60, 32), 50, 5, 0);
				WorldTasks.schedule(new Task() {
					@Override
					public void run() {
						delayHit(npc, -1, target, Hit.magic(npc, getMaxHit(npc, 255, CombatStyle.MAGIC, player)));
					}
				}, p.getTaskDelay());
				if ((Utils.getRandomInclusive(100) > 80) && !player.hasEffect(Effect.FREEZE) && !player.getPrayer().isProtectingMage()) {
					player.spotAnim(369);
					player.freeze(Ticks.fromSeconds(10));
				}
			} else if (attackType == 2) {
				npc.sync(9968, 905);
				WorldProjectile p = World.sendProjectile(npc, target, RANGE_PROJECTILE, new Pair<>(60, 32), 50, 5, 0);
				WorldTasks.schedule(new Task() {
					@Override
					public void run() {
						delayHit(npc, -1, target, Hit.range(npc, getMaxHit(npc, 255, CombatStyle.RANGE, player)));
					}
				}, p.getTaskDelay());
			} else if (attackType == 3) {
				npc.sync(9955, 905);
				WorldProjectile p = World.sendProjectile(npc, target, SPECIAL_PROJECTILE, new Pair<>(60, 32), 50, 5, 0);
				final Tile targetPosition = Tile.of(player.getX(), player.getY(), player.getPlane());
				WorldTasks.schedule(new Task() {
					@Override
					public void run() {
						if ((player.getX() == targetPosition.getX()) && (player.getY() == targetPosition.getY()))
							player.applyHit(new Hit(player, player.getHitpoints() / 2, HitLook.TRUE_DAMAGE));
						World.sendSpotAnim(targetPosition, new SpotAnim(2315));
					}
				}, p.getTaskDelay());
			}
		}
		return 5;
	}

}
