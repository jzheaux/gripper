package com.joshcummings.gripper.command;

import org.bukkit.entity.Player;

import com.joshcummings.gripper.annotation.GripperArguments;
import com.joshcummings.gripper.annotation.GripperCommand;

@GripperCommand(value="ignite", playerOnly=true)
public class SimpleGripperCommandExecutor {
	@GripperArguments
	public void igniteSelf(Player self) {
		self.setFireTicks(1000);
	}
	
	@GripperArguments(value="{player}")
	public void ignite(Player player) {
		player.setFireTicks(1000);
	}
	
	@GripperArguments(value="{player} {time}")
	public void ignite(Player player, Integer time) {
		player.setFireTicks(time);
	}
}
