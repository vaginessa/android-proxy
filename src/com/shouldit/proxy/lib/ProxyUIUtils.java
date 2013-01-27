package com.shouldit.proxy.lib;

import android.content.Context;

import com.shouldit.proxy.lib.APLConstants.ProxyStatusErrors;

public class ProxyUIUtils
{
	
	
	public static String GetStatusTitle(ProxyConfiguration conf, Context callerContext)
	{
		String description;

		switch (conf.getCheckingStatus())
		{
			case CHECKED:
			{
				ProxyStatusErrors status = conf.getMostRelevantProxyStatusError();

				switch (status)
				{
					case NO_ERRORS:
						description = callerContext.getResources().getString(R.string.status_title_enabled);
						break;

					case PROXY_NOT_ENABLED:
						description = callerContext.getResources().getString(R.string.status_title_not_enabled);
						break;

					case PROXY_ADDRESS_NOT_VALID:
						description = callerContext.getResources().getString(R.string.status_title_invalid_address);
						break;

					case PROXY_NOT_REACHABLE:
						description = callerContext.getResources().getString(R.string.status_title_not_reachable);
						break;

					case WEB_NOT_REACHABLE:
						description = callerContext.getResources().getString(R.string.status_title_web_not_reachable);
						break;

					default:
						description = "";
				}

			}
				break;

			case CHECKING:
				description = callerContext.getResources().getString(R.string.status_title_checking);
				break;

			default:
				description = "";
				break;
		}

		return description;
	}

	public static String GetStatusDescription(ProxyConfiguration conf, Context callerContext)
	{
		String description;

		switch (conf.getCheckingStatus())
		{
			case CHECKED:
			{
				ProxyStatusErrors status = conf.getMostRelevantProxyStatusError();

				switch (status)
				{
					case NO_ERRORS:
						description = callerContext.getResources().getString(R.string.status_description_enabled);
						description = description + " " + conf.toShortString();
						break;

					case PROXY_NOT_ENABLED:
						description = callerContext.getResources().getString(R.string.status_description_not_enabled);
						break;

					case PROXY_ADDRESS_NOT_VALID:
						description = callerContext.getResources().getString(R.string.status_description_invalid_address);
						break;

					case PROXY_NOT_REACHABLE:
						description = callerContext.getResources().getString(R.string.status_description_not_reachable);
						break;
					case WEB_NOT_REACHABLE:
						description = callerContext.getResources().getString(R.string.status_description_web_not_reachable);
						break;

					default:
						description = "";
				}

			}
				break;

			case CHECKING:
				description = callerContext.getResources().getString(R.string.status_description_checking);
				break;

			default:
				description = "";
				break;
		}

		return description;
	}

	public static String ProxyConfigToStatusString(ProxyConfiguration conf, Context callerContext)
	{
		String message = String.format("%s", conf.toShortString());

		message += " - " + GetStatusTitle(conf, callerContext);

		return message;
	}
}
