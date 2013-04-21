/*
 * Copyright (C) 2013 Olli Kallioinen
 * http://suikki.wordpress.com/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.github.suikki.wotconnection;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class representing one clan battle in WOT.
 * 
 * @author Olli Kallioinen
 */
public class ClanBattle {

	private long time;
	private int chips;
	private String type;
	private List<Province> provinces;
	private List<String> arenas;
	private boolean started = false;

	ClanBattle() {
	}

	/**
	 * Returns the time of the battle in milliseconds.
	 * 
	 * @return the time of the battle. Returns 0 if the time is not known.
	 */
	public long getStartTime() {
		return time;
	}

	/**
	 * Returns the amount of milliseconds until the beginning of the battle. Returns -1 if the time
	 * is not known.
	 * 
	 * @return the time of the battle.
	 */
	public long getTimeToBattle() {

		if (time == 0) {
			return -1;

		} else {
			long eta = time - System.currentTimeMillis()
					+ WotConnection.getInstance().getServerTimeDifference();
			return (eta < 0 ? 0 : eta);
		}
	}

	/**
	 * @return <code>true</code> if the battle has been scheduled and a true starting time assigned.
	 */
	public boolean hasStarted() {
		return started;
	}

	/**
	 * The number of chips in the battle.
	 * 
	 * @return the chips
	 */
	public int getChips() {
		return chips;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * List of provinces in the battle.
	 * 
	 * @return the provinces
	 */
	public List<Province> getProvinces() {
		return provinces;
	}

	/**
	 * @return the arenas
	 */
	public List<String> getArenas() {
		return arenas;
	}

	static ClanBattle readFromJson(JSONObject jsonObject) throws JSONException {
		ClanBattle battle = new ClanBattle();
		battle.type = jsonObject.getString("type");
		battle.time = jsonObject.getLong("time") * 1000;
		battle.chips = jsonObject.optInt("chips", 0);
		battle.started = jsonObject.optBoolean("started", false);

		// Read the provinces.
		JSONArray provincesArray = jsonObject.getJSONArray("provinces");
		battle.provinces = new ArrayList<Province>(provincesArray.length());

		for (int i = 0; i < provincesArray.length(); i++) {
			battle.provinces.add(Province.readFromJson(provincesArray.getJSONObject(i)));
		}

		// Read the arenas.
		JSONArray arenasArray = jsonObject.getJSONArray("arenas");
		battle.arenas = new ArrayList<String>(arenasArray.length());

		for (int i = 0; i < arenasArray.length(); i++) {
			battle.arenas.add(arenasArray.getString(i));
		}

		return battle;
	}

	/* JSON: from the server
	{
		"provinces" : [{
				"name" : "Troms",
				"id" : "NO_02"
			}
		],
		"started" : false,
		"type" : "landing",
		"time" : 0,
		"arenas" : ["Erlenberg"],
		"chips" : null
	}
	*/
}
