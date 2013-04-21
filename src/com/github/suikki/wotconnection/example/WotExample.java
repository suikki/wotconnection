package com.github.suikki.wotconnection.example;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.github.suikki.wotconnection.ClanBattle;
import com.github.suikki.wotconnection.Province;
import com.github.suikki.wotconnection.WotConnection;

/**
 * Simple example class that is using the wot_api.
 */
public class WotExample {

	public static void main(String[] args) {
		WotConnection wot = WotConnection.getInstance();

		// Get the scheduled battles for our example clan.
		//
		// The id of the clan can be retrieved by going to http://worldoftanks.eu/community/clans/
		// Going to the clan page and taking the last part of the url (Only the number part
		// matters and the rest can be omitted.
		String clanId = "500000218-SPOF";
		
		// NOTE: uses EU server by default. server can be change
		System.out.println("Requesting battles from the EU server.");
		List<ClanBattle> battles = wot.getBattles(clanId);

		if (battles == null) {
			System.out.println("   Sry. Error retrieving battles.");
		} else {
			// Show the server time difference.
			long timeDifference = wot.getServerTimeDifference() / 1000;
			System.out.println("Response received (Server time difference: " + timeDifference + " seconds)\n");

			// Print a list of scheduled battles.
			System.out.println("Battles:");
			printBattles(battles);
		}
	}

	/**
	 * Prints the clan battle info to standard system out.
	 * 
	 * @param battles the battles to be printed.
	 */
	private static void printBattles(List<ClanBattle> battles) {

		// List each battle.
		for (ClanBattle battle : battles) {

			// Figure out the time to battle start.
			String timeString;

			if (battle.hasStarted()) {
				// The accurate battle time is known.
				long timerSeconds = battle.getTimeToBattle() / 1000;
				int minutes = (int) timerSeconds / 60;
				int seconds = (int) timerSeconds % 60;

				// Format the timer.
				timeString = String.format("%dmin %02ds", minutes, seconds);

			} else if (battle.getStartTime() > 0) {
				// Only an estimated time is available. Format the start time of day.
				DateFormat sdf = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT,
						SimpleDateFormat.SHORT);
				timeString = "(estimated) " + sdf.format(new Date(battle.getStartTime()));

			} else {
				// Battle start time is unknown.
				timeString = "Unknown";
			}

			System.out.println("   type: " + battle.getType() + " time: " + timeString + " chips: "
					+ battle.getChips());

			// List the provinces for this battle.
			List<Province> provinces = battle.getProvinces();
			for (Province province : provinces) {
				System.out.println("      province: " + province.getName());
			}

			// List the arenas for this battle.
			List<String> arenas = battle.getArenas();
			for (String arena : arenas) {
				System.out.println("      arena: " + arena);
			}
		}
	}
}
