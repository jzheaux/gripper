package com.joshcummings.gripper.command;

import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.defaults.PluginsCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class GripperPluginExecutorTest {
	private static final Logger logger = Logger.getLogger(GripperPluginExecutorTest.class.getName());
	
	private Plugin plugin = Mockito.mock(Plugin.class);
	private Player current = Mockito.mock(Player.class);
	private Server s = Mockito.mock(Server.class);
	
	@Before
	public void setUp() {
		Mockito.when(plugin.getLogger()).thenReturn(logger);
		Mockito.when(current.getServer()).thenReturn(s);
	}
	
	@After
	public void tearDown() {
		Mockito.reset(plugin, current, s);
	}
	
	@Test
	public void testDefaultIgnite() {
		String targetName = "dave";

		Player dave = Mockito.mock(Player.class);
		Mockito.when(s.getPlayer(targetName)).thenReturn(dave);
		
		Command c = new PluginsCommand("ignite");
		
		GripperCommandExecutor gp = new GripperCommandExecutor(plugin, "com.joshcummings.gripper.command");
		gp.onCommand(current, c, c.getName(), new String[] { targetName });
		
		Mockito.verify(dave, Mockito.times(1)).setFireTicks(1000);
	}

	@Test
	public void testIgniteByCount() {
		String targetName = "dave";
		Integer time = 12000;
		
		Mockito.when(plugin.getLogger()).thenReturn(logger);
		
		Player dave = Mockito.mock(Player.class);
		Mockito.when(s.getPlayer(targetName)).thenReturn(dave);
		
		Command c = new PluginsCommand("ignite");
		
		GripperCommandExecutor gp = new GripperCommandExecutor(plugin, "com.joshcummings.gripper.command");
		gp.onCommand(current, c, c.getName(), new String[] { targetName, String.valueOf(time) });
		
		Mockito.verify(dave, Mockito.times(1)).setFireTicks(time);
	}
	
	@Test
	public void testIgniteSelf() {		
		Mockito.when(plugin.getLogger()).thenReturn(logger);
		
		Player current = Mockito.mock(Player.class);
		
		Command c = new PluginsCommand("ignite");
		
		GripperCommandExecutor gp = new GripperCommandExecutor(plugin, "com.joshcummings.gripper.command");
		gp.onCommand(current, c, c.getName(), new String[0]);
		
		Mockito.verify(current, Mockito.times(1)).setFireTicks(1000);
	}
}
