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
package com.aionemu.commons.scripting.metadata;

import java.lang.annotation.*;

/**
 * Method marked as {@link OnClassUnload} will be called when there is a script reload or shutdown.<br>
 * Only static methods with no arguments can be marked with this annotation.<br>
 * This is only used if {@link com.aionemu.commons.scripting.ScriptContext#getClassListener()} returns instance of
 * {@link com.aionemu.commons.scripting.classlistener.OnClassLoadUnloadListener} subclass.
 *
 * @author SoulKeeper
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnClassUnload
{
}
