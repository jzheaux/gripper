package com.joshcummings.gripper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Apply this to each method that represents a particular way to run your command, e.g.
 * 
 * @GripperArguments("{player}")
 * public void ignite(Player player)
 * 
 * 
 * @author jzheaux
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GripperArguments {
	/**
	 * A list of the tokens in the command, less the command name itself. The list may be
	 * one fewer than the list of method parameters if the last parameter is a command sender, e.g.
	 * 
	 * <strong>Legal Use:</strong>
	 * 
	 * @GripperArguments("{player}")
	 * public igniteFriend(Player player) { ... }
	 * 
	 * @GripperArguments("{player} {time}")
	 * public igniteFriendFor(Player player, Integer time) { ... }
	 * 
	 * @GripperArguments
	 * public igniteSelf(Player commandSender) { ... }
	 * 
	 * <strong>Dodgy Use:</strong>
	 * 
	 * @GripperArguments("{player} {time}")
	 * public igniteFriendFor(Integer time, Player player) { ... }
	 * 
	 * <em>This <strong>may</strong> work, but probably not. The framework does not at this time match argument
	 * names in the annotation to parameter names in the method. Currently, it simply takes the first argument
	 * from the command and matches it to the first argument in the method.</em>
	 * 
	 * <strong>Incorrect Use:</strong>
	 * 
	 * @GripperArguments("{player} {time}")
	 * public igniteFriend(Player player) { ... }
	 * 
	 * <em>Whoops! The number of parameters in the method is fewer than the number of tokens in the command!</em>
	 * 
	 * @GripperArguments("{time}")
	 * public lookAtTheTime(Integer time, Integer aLaterTime) { ... }
	 * 
	 * <em>Whoops! The number of parameters in the method is more than the number of tokens in the command! (If the last
	 * parameter were the player that sent the command, then this would work.)</em>
	 * 
	 * @return
	 */
	String value() default "";
}
