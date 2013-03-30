/**
 * 
 */
package edu.rit.csh.agargiulo.Gatekeeper;

/**
 * @author agargiulo
 * 
 */
public class Gatekeeper
{

	/*
	 * Strings that are internal to the app
	 * basically anything that isn't displayed to the user
	 * which would go in res/values/strings.xml
	 */
	protected static final String PREF_LOGGEDIN = "loggedin";
	protected static final String PREF_USERNAME = "username";
	protected static final String PREF_PASSWORD = "password";

	protected static final String D_STATE_UNKNOWN = "unknown";
	protected static final String D_STATE_LOCKED = "locked";
	protected static final String D_STATE_UNLOCKED = "unlocked";

	protected static final String COLOR_GRAY = "#DDDDDD";
	protected static final String COLOR_RED = "#FF8080";
	protected static final String COLOR_GREEN = "#80C080";

	protected static final String JSON_SUCCESS = "success";
	protected static final String JSON_RESPONSE = "response";
	protected static final String JSON_RESPONSE_NULL = "null";
	protected static final String JSON_RESPONSE_STATE = "state";
	protected static final String JSON_RESPONSE_ID = "id";
	protected static final String JSON_RESPONSE_NAME = "name";
	protected static final String JSON_ERROR = "error";
	protected static final String JSON_ERROR_TYPE = "error_type";
	protected static final String JSON_ERROR_LOGIN = "login";
	protected static final String JSON_ERROR_DENIAL = "denial";
	protected static final String JSON_ERROR_COMMAND = "command";
}
