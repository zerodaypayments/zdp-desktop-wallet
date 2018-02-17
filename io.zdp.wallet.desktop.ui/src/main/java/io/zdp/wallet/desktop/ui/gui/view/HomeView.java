package io.zdp.wallet.desktop.ui.gui.view;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import io.zdp.wallet.api.domain.WalletTransaction;
import io.zdp.wallet.desktop.ui.common.QTextComponentContextMenu;
import io.zdp.wallet.desktop.ui.common.SwingHelper;
import io.zdp.wallet.desktop.ui.gui.dialog.HomePanel;
import io.zdp.wallet.desktop.ui.service.DesktopWalletService;

@Component
public class HomeView {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DesktopWalletService walletService;

	@Value("${app.url.online.faq}")
	private String appUrlOnlineFaq;

	@Value("${app.url.online.help}")
	private String appUrlOnlineHelp;

	public JPanel get() {

		HomePanel homePanel = new HomePanel();

		String balance = walletService.getCurrentWallet().getBalance().toPlainString();
		homePanel.txtBalance.setText(balance);
		SwingHelper.setFontForJText(homePanel.events);

		new QTextComponentContextMenu(homePanel.events);

		// Show recent transactions
		if (walletService.getCurrentWallet() != null) {

			List<WalletTransaction> txs = walletService.getCurrentWallet().getTransactions();

			if (false == CollectionUtils.isEmpty(txs)) {

				StringBuilder sb = new StringBuilder("<html><body>");
				sb.append("<table border='0' width='100%'>");

				sb.append("<tr style='background:#333;color:white;'><th align='right'>Date</th><th  align='left'>Amount</th><th  align='left'>Transaction</th></tr>");

				for (WalletTransaction tx : txs) {

					sb.append("<tr>");

					sb.append("<td nowrap align='right'>");
					sb.append(new SimpleDateFormat("dd MMM yy hh:mm:ss a").format(tx.getDate()));
					sb.append("</td><td  style='background:white;color:black;'>");
					sb.append(tx.getAmount().toPlainString());
					sb.append("</td><td>");
					sb.append("<a href='tx:'" + tx.getUuid() + "'>" + tx.getUuid() + "</a>");
					sb.append("</td>");

					sb.append("</tr>");
				}

				sb.append("</table>");
				sb.append("</body></html>");

				homePanel.events.setText(sb.toString());
			}
		}

		return homePanel;

	}

}
