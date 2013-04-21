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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The main connection class for requesting data from the WOT web service.
 * 
 * @author Olli Kallioinen
 */
public class WotConnection {

	private static WotConnection instance = new WotConnection();
	
	private static final boolean USE_TEST_DATA = false;

	private static final boolean DEBUG = false;

	private String baseUri = "http://cw.worldoftanks.eu";
	
	private long serverTimeDifference = 0;

	private boolean timeDifferenceKnown = false;

	/**
	 * @return the singleton instance of the WotConnection.
	 */
	public static WotConnection getInstance() {
		return instance;
	}

	/**
	 * @return the server time difference to the local machine in milliseconds. Must be called after
	 *         at least one server request has been made.
	 * 
	 * @throws IllegalStateException if the server time is not known because no server requests have
	 *             been made.
	 */
	public long getServerTimeDifference() {
		if (!timeDifferenceKnown) {
			throw new IllegalStateException(
					"Server time difference is not known before a server request is made.");
		}
		return serverTimeDifference;
	}

	/**
	 * Set the server address where to query clan and battle data rom. Defaults to <code>http://cw.worldoftanks.eu</code>.
	 * @param baseUri
	 */
	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}
	
	/**
	 * Returns the currently scheduled clan battles in a list.
	 * 
	 * @return the currently scheduled clan battles in a list.
	 */
	public List<ClanBattle> getBattles(String clanId) {

		if (USE_TEST_DATA) {
			// Use generated testing data.
			timeDifferenceKnown = true;
			return generateTestData();
		}

		// Get the json response from the remote server.
		String url = baseUri + "/clans/" + clanId + "/battles/?type=table";
		JSONObject json = getJson(url);

		if (json == null) {
			return null;
		}
		
		// Parse the battle data from the response.
		try {
			JSONArray battlesArray = json.getJSONObject("request_data").getJSONArray("items");

			ArrayList<ClanBattle> battles = new ArrayList<ClanBattle>(battlesArray.length());

			for (int i = 0; i < battlesArray.length(); i++) {
				battles.add(ClanBattle.readFromJson(battlesArray.getJSONObject(i)));
			}

			return battles;

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	private JSONObject getJson(String url) {

		try {
			if (DEBUG) System.out.println("url: " + url);
			String jsonString = httpRequest(url);
			if (DEBUG) System.out.println("response: " + jsonString);

			if (jsonString != null) {
				return new JSONObject(jsonString);
			}

		} catch (IOException e) {
			e.printStackTrace();

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	private String httpRequest(String url) throws IOException {

		URL requestUrl = new URL(url);
		URLConnection uc = requestUrl.openConnection();

		// Get the server time from the http header.		
		// "EEE, d MMM yyyy HH:mm:ss Z"  == Wed, 4 Jul 2001 12:08:56 -0700
		String date = uc.getHeaderField("Date");
		if (date != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
			try {
				serverTimeDifference = System.currentTimeMillis()
						- dateFormat.parse(date).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			timeDifferenceKnown = true;
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));

		StringBuilder response = new StringBuilder();

		try {

			while (true) {
				String line = in.readLine();

				if (line == null) {
					break;
				} else {
					response.append(line);
				}
			}
			return response.toString();

		} finally {
			in.close();
		}
	}

	private List<ClanBattle> generateTestData() {
		try {
			String[] provinces = new String[] { "Tromssa", "Province X", "Province Y", "Province Z" };
			String[] arenas = new String[] { "Erlenberg", "Arena X", "Arena Y", "Arena Z" };
			long[] times = new long[] { System.currentTimeMillis() / 1000 + 12 * 60 + 15,
					System.currentTimeMillis() / 1000 + 17 * 60 + 45,
					System.currentTimeMillis() / 1000 / 60 / 60 * 60 * 60 + 62 * 60, 0 };

			ArrayList<ClanBattle> battles = new ArrayList<ClanBattle>(provinces.length);

			for (int i = 0; i < provinces.length; i++) {
				ClanBattle battle = ClanBattle.readFromJson(new JSONObject("{" + //
						"\"arenas\" : [\"" + arenas[i] + "\"]," + //
						"\"started\" : false," + //
						"\"type\" : \"landing\"," + //
						"\"provinces\" : [{" + //
						"\"name\" : \"" + provinces[i] + "\"," + //
						"\"id\" : \"LV_02\"" + //

						"}" + //
						"]," + //
						"\"chips\" : 15," + //
						"\"time\" : " + times[i] + //
						"}"));
				battles.add(battle);
			}

			return battles;

		} catch (JSONException e) {
			throw new IllegalStateException(e);
		}
	}
}
