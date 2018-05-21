package io.zdp.wallet.desktop.ui.gui.view;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent.EventType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import io.zdp.api.model.v1.GetTransactionDetailsResponse;
import io.zdp.client.ZdpClient;
import io.zdp.wallet.api.db.domain.AccountTransaction;
import io.zdp.wallet.desktop.ui.common.Alert;
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

	@Autowired
	private ZdpClient zdp;

	@Autowired
	private TxDetailsViewPopup detailsPopup;

	public JPanel get() {

		HomePanel homePanel = new HomePanel();

		String balance = "nan";// walletService.getCurrentWallet().getBalance().toPlainString();
		homePanel.txtBalance.setText(balance);
		SwingHelper.setFontForJText(homePanel.events);

		new QTextComponentContextMenu(homePanel.events);

		// Show recent transactions
		if (walletService.getCurrentWallet() != null) {

			List<AccountTransaction> txs = Collections.emptyList();

			if (false == CollectionUtils.isEmpty(txs)) {

				StringBuilder sb = new StringBuilder("<html><body>");
				sb.append("<table border='0' width='100%'>");

				sb.append("<tr style='background:#333;color:white;'><th align='right'>Date</th><th  align='left'>Amount</th><th  align='left'>Transaction</th></tr>");

				for (AccountTransaction tx : txs) {

					sb.append("<tr>");

					sb.append("<td nowrap align='right'>");
					sb.append(new SimpleDateFormat("dd MMM yy hh:mm:ss a").format(tx.getDate()));
					sb.append("</td><td  style='background:white;color:black;'>");
					sb.append(tx.getAmount().toPlainString());
					sb.append("</td><td>");
					sb.append("<a href='" + tx.getUuid() + "'>" + tx.getUuid() + "</a>");
					sb.append("</td>");

					sb.append("</tr>");
				}

				sb.append("</table>");
				sb.append("</body></html>");

				homePanel.events.setText(sb.toString());

				homePanel.events.addHyperlinkListener(e -> {

					if (e.getEventType().equals(EventType.ACTIVATED)) {

						String txUuid = e.getDescription();

						try {

							AccountTransaction tx = null;// walletService.getCurrentWallet().getTxByUuid(txUuid);

							if (tx != null && tx.hasAllDetails() == false) {

								log.debug("Get tx details from network: " + txUuid);
								
								GetTransactionDetailsResponse details = zdp.getTransactionDetails(txUuid);

								if (tx != null && details != null) {
									tx.setFee(new BigDecimal(details.getFee()));
									tx.setFrom(details.getFrom());
									tx.setTo(details.getTo());
									tx.setMemo(details.getMemo());
									log.debug("Tx: " + tx);
									
									walletService.saveCurrentWallet();
								}
							}

							detailsPopup.show(tx);

						} catch (Exception e1) {
							log.error("Error: ", e1);
							Alert.error("Cannot retrieve transaction details");
						}
					}
				});
			}
		}

		return homePanel;

	}

}
