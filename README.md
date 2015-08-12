Gripper is a library that makes writing command-driven Bukkit plugins easier.

Configuration
-------------

Gripper is a Bukkit library. It thus requires Bukkit in order to compile. In addition,
it requires the org.reflections Reflections library dependency.

Usage
-----

The demo class to create the Ingite-Player plugin on the bukkit website is hundreds of
lines long. As I have used this example to try and teach new programmers about coding
Minecraft plugins, they quickly become discouraged at the number of ideas they need to master in order to understand, tweak, and reproduce what they have just copied from
the tutorial.

Gripper takes this boilerplate code and abstracts it away into a simple CommandExecutor
that understands two annotations intended to allow the user to describe his command
options in the context of isolated method signatures.

For example, consider the following class which replicates the Ignite-Player plugin, this
time using Gripper:

```java
	package com.joshcummings.ignite.gripper;
	
	import org.bukkit.entity.Player;
	
	import com.joshcummings.gripper.annotation.GripperArguments;
	import com.joshcummings.gripper.annotation.GripperCommand;
	
	@GripperCommand(name="ignite", playerOnly=true)
	public class IgniteCommands {
		@GripperArguments("{player}")
		public void ignitePlayer(Player player) {
			player.setFireTicks(1000);
		}
	}
```

After that, it is simply a matter of initializing the GripperCommandExecutor, which
will do the rest of the work:

```java
	public class YourPlugin extends JavaPlugin {
		private GripperCommandExecutor executor;

		public void onEnable() {
			executor = new GripperCommandExecutor
				(this, "com.joshcummings.ignite.gripper");
		}	
	
		// ...
	
		public boolean onCommand(CommandSender commandSender, Command cmd,
			String label, String[] params) {
			return executor.onCommmand(commmandSender, cmd, label, params);
		}
	}
```

The GripperCommandExecutor, on construction, will scan the package specified for any
classes using the Gripper annotations. It will construct those classes and cache them,
awaiting the invocation of its onCommand method.

Once the onCommand method is invoked, Gripper will search through its instances and match
the commmand sent against the content of all the GripperCommand annotations that it found.
Then, it will match the method against the appropriate number of arguments the user
specified. Gripper will coerce that commmand argument from the user into the appropriate
data type.

Commands with multiple argument permutations
--------------------------------------------

IgniteCommands can now be expanded to support more features with ease:

```java
	package com.joshcummings.gripper.command;
	
	import org.bukkit.entity.Player;
	
	import com.joshcummings.gripper.annotation.GripperArguments;
	import com.joshcummings.gripper.annotation.GripperCommand;
	
	@GripperCommand(value="ignite", playerOnly=true)
	public class IgniteCommands {
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
```

The first new command, igniteSelf, shows that the last argument in the method signature may always be
the current player; no GripperArgument need be specified.

The second new command, ignite(player,time), shows that adding parameters to a command
is as simple as creating the appropriate method and the appropriate list of {}
parameter names.

Why do I need to specify the arguments twice?
---------------------------------------------

The thought behind the apparent duplication of command arguments in the parameter list as
well as the annotation is first to make it easier for the GripperCommandExecutor to match
when there is an extra parameter for the current player. Also, it anticipates the 
possibility of treating the argument list as a template, allowing for a given parameter
to be listed more than once or possibly interpreted in more than one way. These features
don't exist yet but are not out of the question.


