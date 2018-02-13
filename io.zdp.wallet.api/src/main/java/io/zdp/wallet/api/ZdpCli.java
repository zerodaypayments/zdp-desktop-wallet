package io.zdp.wallet.api;

import java.io.File;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;

import io.zdp.api.model.BalanceResponse;
import io.zdp.api.model.TransferDetails;
import io.zdp.api.model.TransferDetailsList;
import io.zdp.client.ZdpClient;
import io.zdp.common.crypto.CryptoUtils;
import io.zdp.common.utils.Mnemonics;
import io.zdp.common.utils.Mnemonics.Language;
import io.zdp.wallet.api.domain.Wallet;
import io.zdp.wallet.api.service.WalletService;

/*
 * java -jar zdp.jar
 * zdp --help
 * zdp --ping
 * zdp --fee
 * zdp --createwallet --file FILE --pass PASS
 * zdp --restorewallet -f 1.wallet (ask for seed/list of mnemonics in interractive mode)
 * zdp --balance -f 1.wallet -p pass
 * zdp --txuuid <uuid> 
 * zdp --txfrom <address> or <hash>
 * zdp --txto <address> or <hash>
 */
@Component
public class ZdpCli {

	@Autowired
	private ZdpClient zdp;

	public static void main(final String... _args) {

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/spring-context.xml");

		try {
			ctx.getBean(ZdpCli.class).run(_args);
		} catch (BeansException e) {
			e.printStackTrace();
		} finally {
			ctx.stop();
			ctx.close();
		}

	}

	private void run(String[] _args) {

		Options options = new Options();

		
		options.addOption(Option.builder().longOpt("help").desc("display this help and exit").hasArg(false).build());

		options.addOption(Option.builder().longOpt("ping").desc("ping ZDP network").hasArg(false).build());

		options.addOption(Option.builder().longOpt("fee").desc("get current network transaction fee").hasArg(false).build());

		options.addOption(Option.builder().desc("create a new wallet as a <FILE> encrypted <PASSWORD>").hasArg(false).longOpt("createwallet").build());

		options.addOption(Option.builder().desc("wallet's password").hasArg(true).longOpt("pass").argName("PASSWORD").build());

		options.addOption(Option.builder().desc("wallet's file").hasArg(true).longOpt("file").argName("FILE").build());

		options.addOption(Option.builder().desc("get transaction details by specified UUID").hasArg(true).longOpt("txuuid").build());

		options.addOption(Option.builder().desc("get transaction details by specified FROM address").hasArg(true).longOpt("txfrom").build());

		options.addOption(Option.builder().desc("get transaction details by specified TO address").hasArg(true).longOpt("txto").build());
		
		options.addOption(Option.builder().longOpt("balance").desc("check wallet balance by specified wallet <FILE> and <PASSWORD>").hasArg(false).build());

		CommandLineParser parser = new DefaultParser();

		try {

			CommandLine cmd = parser.parse(options, _args);

			// validate that block-size has been set
			if (cmd.hasOption("help")) {
				help(options);
			} else if (cmd.hasOption("ping")) {
				ping();
			} else if (cmd.hasOption("fee")) {
				fee();
			} else if (cmd.hasOption("createwallet")) {
				createWallet(options, cmd);
			} else if (cmd.hasOption("txuuid")) {
				getTransactionDetailsByUuid(cmd);
			} else if (cmd.hasOption("txfrom")) {
				getTransactionDetailsByFrom(cmd);
			} else if (cmd.hasOption("txto")) {
				getTransactionDetailsByTo(cmd);
			} else if (cmd.hasOption("balance")) {
				
				final String password = cmd.getOptionValue("pass");

				if (StringUtils.isBlank(password)) {
					System.err.println("Please specify wallet's <PASSWORD> ");
					help(options);
				}
				
				String f = cmd.getOptionValue("file");

				if (StringUtils.isBlank(f)) {
					System.err.println("Please specify wallet's <FILE>");
					help(options);
				}

				File file = new File(f);

				if (false == file.canRead()) {
					System.err.println("Cannot read \"" + f + "\"");
					System.exit(1);
				}				
				
				Wallet w = WalletService.load(file, password);
				
				if (w==null) {
					System.err.println("Wallet cannot be loaded: " + file.getAbsolutePath());
					System.exit(1);
				}
				
				BalanceResponse balance = zdp.getAccountBalance(w.getPublicKey(), w.getPrivateKey());
				
				System.out.println(getJsonMapper().writeValueAsString(balance));
				
			}

		} catch (ParseException e) {
			e.printStackTrace();
			help(options);
		} catch (NoSuchAlgorithmException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			if (e instanceof ConnectException || e.getCause() instanceof ConnectException) {
				System.err.println("Network not available.");
			} else {
				System.err.println(e.getMessage());
			}
		}

	}

	private void getTransactionDetailsByTo(CommandLine cmd) throws Exception {
		String to = cmd.getOptionValue("txto");
		if (StringUtils.isNotBlank(to)) {
			TransferDetailsList txs = zdp.getTransactionByToAddress(to);
			if (txs != null) {
				System.out.println(getJsonMapper().writeValueAsString(txs.getDetails()));
			}
		}
	}

	private void getTransactionDetailsByFrom(CommandLine cmd) throws Exception {
		String from = cmd.getOptionValue("txfrom");
		if (StringUtils.isNotBlank(from)) {
			TransferDetailsList txs = zdp.getTransactionByFromAddress(from);
			if (txs != null) {
				System.out.println(getJsonMapper().writeValueAsString(txs.getDetails()));
			}
		}
	}

	private void getTransactionDetailsByUuid(CommandLine cmd) throws Exception, JsonProcessingException {
		String tx = cmd.getOptionValue("txuuid");
		if (StringUtils.isNotBlank(tx)) {
			TransferDetails transaction = zdp.getTransaction(tx);
			if (transaction != null) {
				System.out.println(getJsonMapper().writeValueAsString(transaction));
			}
		}
	}

	private ObjectMapper getJsonMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		return mapper;
	}

	private void fee() throws Exception {
		BigDecimal fee = zdp.getFee();
		System.out.println(fee.toPlainString());
	}

	private void ping() throws Exception {
		System.out.println("Pinging ZDP network");
		while (true) {
			long st = System.currentTimeMillis();
			zdp.ping();
			long et = System.currentTimeMillis();
			System.out.println("time=" + (et - st) + "ms");
			Thread.sleep(1000);
		}
	}

	private void createWallet(Options options, CommandLine cmd) throws NoSuchAlgorithmException, Exception {
		String password = cmd.getOptionValue("pass");

		if (StringUtils.isBlank(password)) {
			System.err.println("Please specify a <PASSWORD> for a new wallet");
			help(options);
		}

		Zxcvbn zxcvbn = new Zxcvbn();
		Strength strength = zxcvbn.measure(password);

		if (strength.getScore() < 3) {
			System.err.println("Please use a stronger password. " + strength.getFeedback().getWarning());
			System.exit(1);
		}

		String f = cmd.getOptionValue("file");

		if (StringUtils.isBlank(f)) {
			System.err.println("Please specify a <FILE> for a new wallet");
			help(options);
		}

		File file = new File(f);

		if (false == file.canWrite()) {
			System.err.println("Cannot write to \"" + f + "\"");
			System.exit(1);
		}

		String seed = CryptoUtils.generateRandomNumber256bits();

		WalletService.create(seed, file, password);

		System.out.println("Wallet created: " + file.getAbsolutePath());
		System.out.println("Private key: " + seed);
		System.out.println("List of mnemonics to restore this wallet: ");
		List<String> words = Mnemonics.generateWords(Language.ENGLISH, seed);
		System.out.println("----------------------");
		for (String w : words) {
			System.out.println(w);
		}
		System.out.println("----------------------");
	}

	private void help(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar zdp.jar", options);
		System.exit(0);
	}

}
