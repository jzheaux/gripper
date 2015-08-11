package com.joshcummings.gripper.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;

import com.joshcummings.gripper.annotation.GripperArguments;
import com.joshcummings.gripper.annotation.GripperCommand;

/**
 * Instantiate this class as a private member variable in your Bukkit Plugin. It will scan
 * the package you indicate for any classes annotated with GripperCommand and execute them on your behalf.
 * 
 * In your plugin, simply call {@link GripperCommandExecutor#onCommand(CommandSender, Command, String, String[])}
 * in your onCommand method.
 * 
 * @author jzheaux
 *
 */
public class GripperCommandExecutor implements CommandExecutor {
	private Reflections reflections;
	private Plugin plugin;
	
	protected Map<String, Object> commands = new HashMap<>();
	
	public GripperCommandExecutor(Plugin plugin, String packageName) {
		reflections = new Reflections(packageName);
		this.plugin = plugin;
		
		Set<Class<?>> commandExecutors = reflections.getTypesAnnotatedWith(GripperCommand.class);
		for ( Class<?> command : commandExecutors ) {
			try {
				Object obj = command.newInstance();
				GripperCommand c = obj.getClass().getAnnotation(GripperCommand.class);
				commands.put(c.value(), obj);
			} catch (InstantiationException | IllegalAccessException e) {
				plugin.getLogger().info("Couldn't pull in command executor [" + command + "]: " + e.getMessage());
			}
		}
	}

	protected Method findMatchingMethod(Object commandExecutor, String name, Object[] params, CommandSender sender) {
		Method[] methods = commandExecutor.getClass().getDeclaredMethods();
		for ( Method m : methods ) {
			GripperArguments c = m.getAnnotation(GripperArguments.class);
			if ( c != null ) {
				String cmdPattern = c.value();
				String[] cmdParams = cmdPattern.isEmpty() ? new String[0] : cmdPattern.split(" ");
				if ( cmdParams.length == params.length ) {
					Parameter[] types = m.getParameters();
					if ( types.length - params.length == 1 && CommandSender.class.isAssignableFrom(types[types.length - 1].getType()) ) {
						return m;
					} else if ( types.length == params.length ) {
						return m;
					}
				}
			}
		}
		
		return null;
	}
	
	protected boolean executeMethod(Object commandExecutor, Method m, Object[] params, CommandSender sender) {
		Parameter[] types = m.getParameters();
		Object[] methodParams = new Object[types.length];
		
		for ( int i = 0; i < params.length; i++ ) {
			Parameter type = types[i];
			String param = (String)params[i];
			if ( type.getType().equals(String.class) ) {
				methodParams[i] = param;
			} else if ( type.getType().equals(Integer.class) ) {
				methodParams[i] = Integer.parseInt(param);
			} else if ( type.getType().equals(Double.class) ) {
				methodParams[i] = Double.parseDouble(param);
			} else if ( type.getType().equals(Player.class) ) {
				methodParams[i] = sender.getServer().getPlayer(param);
				if ( methodParams[i] == null ) {
					plugin.getLogger().info("Player [" + param + "] is not logged in.");
					return false;
				}
			} else if ( type.getType().equals(Material.class) ) {
				methodParams[i] = Material.valueOf(param);
			}
		}

		if ( types.length - params.length == 1 ) {
			methodParams[params.length] = sender;
		}
		
		try {
			m.invoke(commandExecutor, methodParams);
			return true;
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			plugin.getLogger().info("Command failed: [" + e.getMessage() + "]");
			return false;
		}
	}

	/**
	 * Call this method in your onCommand method in your bukkit plugin and it will find the 
	 * correct Gripper-annotated class to invoke.
	 * 
	 * @return - Whether or not the command's usage screen should be shown
	 */
	@Override
	public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] params) {
		Object commandExecutor = commands.get(cmd.getName());
		
		GripperCommand gc = commandExecutor.getClass().getAnnotation(GripperCommand.class);
		if ( gc.playerOnly() && !( commandSender instanceof Player ) ) {
			commandSender.sendMessage("Only players can set other players on fire.");
			commandSender.sendMessage("This is an arbitrary requirement for demonstration purposes only.");
			return true;
		}
		
		Method m = findMatchingMethod(commandExecutor, cmd.getName(), params, commandSender);
		if ( m != null ) {
			return executeMethod(commandExecutor, m, params, commandSender);
		}
		return false;
	}
}
