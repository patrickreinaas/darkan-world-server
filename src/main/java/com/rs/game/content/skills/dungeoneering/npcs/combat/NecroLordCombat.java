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
package com.rs.game.content.skills.dungeoneering.npcs.combat;

import com.rs.game.World;
import com.rs.game.content.combat.CombatStyle;
import com.rs.game.content.skills.dungeoneering.npcs.NecroLord;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import kotlin.Pair;

import java.util.LinkedList;
import java.util.List;

public class NecroLordCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Necrolord" };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		final NecroLord boss = (NecroLord) npc;

		if (Utils.random(10) == 0) {
			final int skeletonCount = boss.getManager().getParty().getTeam().size();
			final List<Tile> projectileTile = new LinkedList<>();
			WorldTasks.scheduleTimer((ticks) -> {
				if (ticks == 2) {
					for (int i = 0; i < skeletonCount; i++) {
						Tile tile = World.getFreeTile(boss.getManager().getTile(boss.getReference(), Utils.random(2) == 0 ? 5 : 10, 5), 4);
						projectileTile.add(tile);
						World.sendProjectile(boss, tile, 2590, new Pair<>(65, 0), 30, 10, 16);
					}
				} else if (ticks == 4) {
					for (Tile tile : projectileTile)
						boss.addSkeleton(tile);
					return false;
				}
				return true;
			});
		}

		final int attack = Utils.random(4);
		switch (attack) {
			// main attack
			case 0, 1 -> {
				npc.setNextAnimation(new Animation(14209));
				npc.setNextSpotAnim(new SpotAnim(2716));
				World.sendProjectile(npc, target, 2721, new Pair<>(38, 18), 50, 5, 0);
				delayHit(npc, 1, target, Hit.magic(npc, getMaxHitFromAttackStyleLevel(npc, CombatStyle.MAGIC, target)));
				target.setNextSpotAnim(new SpotAnim(2726, 75, 80));
			}
			case 2, 3 -> {
				final Tile tile = Tile.of(target.getTile());
				npc.setNextAnimation(new Animation(attack == 2 ? 710 : 729));
				npc.setNextSpotAnim(new SpotAnim(attack == 2 ? 177 : 167, 0, 65));
				World.sendProjectile(npc, tile, attack == 2 ? 178 : 168, new Pair<>(40, 18), 55, 10, 5);
				WorldTasks.scheduleTimer(1, (ticks) -> {
					for (Entity t : boss.getPossibleTargets()) {
						int damage = getMaxHit(boss, boss.getMaxHit(), CombatStyle.MAGIC, t);
						if (!t.withinDistance(tile, 1))
							continue;
						if (damage > 0) {
							if (attack == 2)
								t.freeze(8);
							else {
								if (t instanceof Player p2) {
									p2.sendMessage("You feel weary.");
									p2.setRunEnergy((int) (p2.getRunEnergy() * .5));
								}
								t.applyHit(new Hit(boss, Utils.random(boss.getMaxHit()) + 1, HitLook.MAGIC_DAMAGE));
							}
							t.setNextSpotAnim(new SpotAnim(attack == 2 ? 179 : 169, 60, 65));
						}
					}
					return false;
				});
			}
		}
		return Utils.random(2) == 0 ? 4 : 5;
	}
}
