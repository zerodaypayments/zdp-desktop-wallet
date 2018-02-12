package io.zdp.wallet.api;

import java.io.File;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import io.zdp.common.crypto.CryptoUtils;
import io.zdp.wallet.api.service.WalletService;

/*
 * java -jar zdp.jar
 * -h <host>
 * zdp --createwallet --file FILE --pass PASS
 * zdp -c restore -f 1.wallet (ask for seed/list of mnemonics in interractive mode)
 * zdp -c balance -f 1.wallet -p pass
 * zdp -c fee 
 * zdp -c tx -uuid <uuid> 
 * zdp -c tx -from <address>
 * zdp -c tx -to <address>
 * zdp -c tx -from <address>
 * zdp -c tx -from <address>
 */
@Component
public class ZdpCli {

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
		
		options.addOption( Option.builder()
				.longOpt("help")
                .desc( "display this help and exit" )
                .hasArg(false)
                .build() );
		
		options.addOption( Option.builder()
                .desc( "create a new wallet, write to the specified FILE and encrypt with a specified PASSWORD" )
                .hasArg(false)
                .longOpt("createwallet")
                .build() );
		
		options.addOption( Option.builder()
                .desc( "wallet's password" )
                .hasArg(true)
                .longOpt("pass")
                .argName("PASSWORD")
                .build() );	
		
		options.addOption( Option.builder()
                .desc( "wallet's file" )
                .hasArg(true)
                .longOpt("file")
                .argName("FILE")
                .build() );				
		
		CommandLineParser parser = new DefaultParser();

		try {

			CommandLine cmd = parser.parse(options, _args);

			// validate that block-size has been set
			if (cmd.hasOption("help")) {
				help(options);
			} else if (cmd.hasOption("createwallet")) {
				String password = cmd.getOptionValue("pass");
				String file = cmd.getOptionValue("file");
				String seed = CryptoUtils.generateRandomNumber256bits();
				WalletService.create(seed, new File(file));
			}

		} catch (ParseException e) {
			e.printStackTrace();
			help(options);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	private void help(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar zdp.jar", options);
		System.exit(0);
	}

}
