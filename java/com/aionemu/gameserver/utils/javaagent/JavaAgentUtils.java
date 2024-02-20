package com.aionemu.gameserver.utils.javaagent;

import com.aionemu.commons.callbacks.Callback;
import com.aionemu.commons.callbacks.CallbackResult;
import com.aionemu.commons.callbacks.EnhancedObject;
import com.aionemu.commons.callbacks.metadata.GlobalCallback;
import com.aionemu.commons.callbacks.metadata.ObjectCallback;
import com.aionemu.commons.callbacks.util.GlobalCallbackHelper;

public class JavaAgentUtils {
	static
	{
		GlobalCallbackHelper.addCallback(new CheckCallback());
	}

	public static boolean isConfigured() {
		JavaAgentUtils jau = new JavaAgentUtils();
		if(!(jau instanceof EnhancedObject))
			throw new Error("Please configure -javaagent jvm option.");

		if(!checkGlobalCallback())
			throw new Error("Global callbacks are not working correctly!");

		((EnhancedObject)jau).addCallback(new CheckCallback());
		if(!jau.checkObjectCallback())
			throw new Error("Object callbacks are not working correctly!");

		return true;
	}

	@GlobalCallback(CheckCallback.class)
	private static boolean checkGlobalCallback()
	{
		return false;
	}

	@ObjectCallback(CheckCallback.class)
	private boolean checkObjectCallback()
	{
		return false;
	}

	@SuppressWarnings("rawtypes")
	public static class CheckCallback implements Callback{

		@Override
		public CallbackResult<Boolean> beforeCall(Object obj, Object[] args) {
			return CallbackResult.newFullBlocker(true);
		}

		@Override
		public CallbackResult<Boolean> afterCall(Object obj, Object[] args, Object methodResult) {
			return CallbackResult.newContinue();
		}

		@Override
		public Class<? extends Callback> getBaseClass() {
			return CheckCallback.class;
		}
	}
}
