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
package com.rs.utils.spawns;

import com.rs.game.World;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;

public class ItemSpawn {

	private final String comment;
	private final int itemId;
	private final int amount;
	private final Tile tile;
	private final int respawnTicks;

	public ItemSpawn(int itemId, int amount, Tile tile, int respawnTicks, String comment) {
		this.itemId = itemId;
		this.amount = amount;
		this.tile = tile;
		this.respawnTicks = respawnTicks;
		this.comment = comment;
	}

	@SuppressWarnings("deprecation")
	public void spawn() {
		World.addGroundItemForever(new Item(itemId, amount), tile, getRespawnTicks());
	}

	public Tile getTile() {
		return tile;
	}

	public int getItemId() {
		return itemId;
	}

	public int getAmount() {
		return amount;
	}

	public String getComment() {
		return comment;
	}

	public int getRespawnTicks() {
		return respawnTicks <= 0 ? 25 : respawnTicks;
	}
}
