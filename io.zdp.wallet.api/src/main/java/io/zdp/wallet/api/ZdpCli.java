package io.zdp.wallet.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;

/*
 * java -jar zdp.jar
 */
public class ZdpCli {

	private static final String COMMAND_TX = "tx";
	private static final String COMMAND_ADDRESS = "address";
	private static final String COMMAND_WALLET = "wallet";

	public static void main(final String... _args) {

		try (Scanner scanner = new Scanner(System.in)) {

			final List<String> args = Collections.unmodifiableList(Arrays.stream(_args).collect(Collectors.toList()));

			final List<String> commands = Collections.unmodifiableList(new ArrayList<>(Arrays.asList(COMMAND_WALLET, COMMAND_ADDRESS, COMMAND_TX)));

			String userCommand = null;

			if (args.isEmpty() || commands.contains(args.get(0)) == false) {

				while (userCommand == null || commands.contains(userCommand) == false) {

					System.out.println("-=-=-=-=-=-=-=-=-=-");
					System.out.println("Available commands:");
					System.out.println("-=-=-=-=-=-=-=-=-=-");

					int index = 0;

					for (String command : commands) {
						System.out.println("[" + (++index) + "] " + command);
					}

					System.out.println("-=-=-=-=-=-=-=-=-=-");

					String userValue = scanner.next();

					int userCommandIndex = NumberUtils.toInt(userValue, -1);

					if (userCommandIndex >= 1 && userCommandIndex <= commands.size()) {
						userCommand = commands.get(userCommandIndex - 1);
					} else if (commands.contains(userValue)) {
						userCommand = userValue;
					}

				}
			}

			System.out.println("Seleted command: " + userCommand);

			if (COMMAND_ADDRESS.equals(userCommand)) {

			} else if (COMMAND_WALLET.equals(userCommand)) {

			} else if (COMMAND_TX.equals(userCommand)) {

				System.out.println("Enter transaction UUID: ");
				String uuid = scanner.next();
				System.out.println("Checking transaction [" + uuid + "]");
			}

		}

	}

}
