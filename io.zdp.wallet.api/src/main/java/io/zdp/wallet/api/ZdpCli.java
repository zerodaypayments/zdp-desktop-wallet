package io.zdp.wallet.api;

import java.io.File;

import io.zdp.common.utils.Mnemonics;
import io.zdp.common.utils.Mnemonics.Language;
import io.zdp.wallet.api.domain.Wallet;
import io.zdp.wallet.api.service.WalletService;

public class ZdpCli {

	public static void main(String[] args) {

		System.out.println("Usage: " + args.length);

		if (args.length == 0) {
			printHelp();
			return;
		}

		if (args[0].equals("wallet")) {

			if (args[1].equals("create")) {

				File file = new File(args[2]);

				try {

					boolean newFile = file.createNewFile();

					if (!newFile) {

						System.err.println("The file already exists: " + file);

					} else {

						Wallet w = WalletService.create(null, file);

						System.out.println("Created wallet: " + file);
						System.out.println("UUID: " + w.getUuid());
						System.out.println("Private key: " + w.getSeed());
						System.out.println("Seed words: " + Mnemonics.generateWords(Language.ENGLISH, w.getSeed()));

					}

				} catch (Exception e) {
					System.err.println("Can't create wallet: " + e.getMessage());
				}
			}
		}

	}

	private static void printHelp() {
		System.out.println("Print help here");
	}

}
