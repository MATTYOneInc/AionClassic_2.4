/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.commons.scripting;

import com.aionemu.commons.scripting.classlistener.ClassListener;

import java.io.File;
import java.util.Collection;

/**
 * This class represents script context that can be loaded, unloaded, etc...<br>
 */
public interface ScriptContext
{
	/**
	 * Initializes script context. Calls the compilation task.<br>
	 * After compilation static methods marked with {@link com.aionemu.commons.scripting.metadata.OnClassLoad} are invoked
	 */
	void init();

	/**
	 * Notifies all script classes that they must save their data and release resources to prevent memory leaks. It's done
	 * via static methods with {@link com.aionemu.commons.scripting.metadata.OnClassUnload} annotation
	 */
	void shutdown();

	/**
	 * Invokes {@link #shutdown()}, after that invokes {@link #init()}. Root folder remains the same, but new compiler and
	 * classloader are used.
	 */
	void reload();

	/**
	 * Returns the root directory for script engine. Only one script engine per root directory is allowed.
	 *
	 * @return root directory for script engine
	 */
	File getRoot();

	/**
	 * Returns compilation result of this script context
	 *
	 * @return compilation result
	 */
	CompilationResult getCompilationResult();

	/**
	 * Returns true if this script context is loaded
	 *
	 * @return true if context is initialized
	 */
	boolean isInitialized();

	/**
	 * Sets files that represents jar files, they will be used as libraries
	 *
	 * @param files that points to jar file, will be used as libraries
	 */
	void setLibraries(Iterable<File> files);

	/**
	 * Returns list of files that are used as libraries for this script context
	 *
	 * @return list of libraries
	 */
	Iterable<File> getLibraries();

	/**
	 * Returns parent script context of this context. Returns null if none.
	 *
	 * @return parent Script context of this context or null
	 */
	ScriptContext getParentScriptContext();

	/**
	 * Returns list of child contexts or null if no contextes present
	 *
	 * @return list of child contexts or null
	 */
	Collection<ScriptContext> getChildScriptContexts();

	/**
	 * Adds child contexts to this context. If this context is initialized - chiled context will be initialized
	 * immideatly. In other case child context will be just added and initialized when {@link #init()} would be called.
	 * Duplicated child contexts are not allowed, in such case child will be ignored
	 *
	 * @param context child context
	 */
	void addChildScriptContext(ScriptContext context);

	/**
	 * Sets the class listener for this script context.
	 *
	 * @param cl class listener
	 */
	void setClassListener(ClassListener cl);

	/**
	 * Returns class listener associated with this ScriptContext.<br>
	 * If it's null - returns parent classListener.<br>
	 * If parent is null and classListener is null - it will add the following class listeners
	 * as default implementation (order saved):
	 * <pre>
	 *     	AggregatedClassListener acl = new AggregatedClassListener();
	 * acl.addClassListener(new OnClassLoadUnloadListener());
	 * acl.addClassListener(new ScheduledTaskClassListener());
	 * </pre>
	 *
	 * @return Associated class listener
	 * @see com.aionemu.commons.scripting.classlistener.AggregatedClassListener
	 * @see com.aionemu.commons.scripting.classlistener.ScheduledTaskClassListener
	 */
	ClassListener getClassListener();

	/**
	 * Sets compiler class name for this script context.<br>
	 * Compiler is not inherrited by children.<br>
	 *
	 * @param className compiler class name
	 */
	void setCompilerClassName(String className);

	/**
	 * Returns compiler class name that will be used for this script context.
	 *
	 * @return compiler class name that will be used for tis script context
	 */
	String getCompilerClassName();

	/**
	 * Tests if this ScriptContext is equal to another ScriptContext. Comparation is done by comparing root files and
	 * parent contexts (if there is any parent)
	 *
	 * @param obj object to compare with
	 * @return result of comparation
	 */
	@Override
	boolean equals(Object obj);

	/**
	 * Returns hashCoded of this ScriptContext. Hashcode is calculated using root file and parent context(if available)
	 *
	 * @return hashCode
	 */
	@Override
	int hashCode();

	/**
	 * This method overrides finalization to ensure that active script context will not be collected by GC. If such
	 * situation happens - {@link #shutdown()} is called to ensure that resources were released.
	 *
	 * @throws Throwable if something goes wrong during finalization
	 */
	void finalize() throws Throwable;
}
