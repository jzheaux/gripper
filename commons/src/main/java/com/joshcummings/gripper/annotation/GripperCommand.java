package com.joshcummings.gripper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Place this anntotation at the top of a class that represents a single command in your Bukkit plugin.
 * 
 * @author jzheaux
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GripperCommand {
	/**
	 * The name of the command as listed in the plugin.yml file
	 * 
	 * @return
	 */
	String value();
	
	/**
	 * Whether or not this command can be run only by players (otherwise it can be run by servers AND players)
	 * 
	 * @return
	 */
	boolean playerOnly() default false;
}
