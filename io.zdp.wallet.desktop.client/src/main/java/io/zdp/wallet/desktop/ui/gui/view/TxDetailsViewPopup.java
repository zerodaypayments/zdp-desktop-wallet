package io.zdp.wallet.desktop.ui.gui.view;

import java.awt.Dimension;

import javax.swing.JDialog;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.wallet.api.domain.AccountTransaction;
import io.zdp.wallet.desktop.ui.common.QTextComponentContextMenu;
import io.zdp.wallet.desktop.ui.common.SwingHelper;
import io.zdp.wallet.desktop.ui.gui.MainWindow;
import io.zdp.wallet.desktop.ui.gui.dialog.TxDetailsPanel;

@Component
public class TxDetailsViewPopup {

	@Autowired
	private MainWindow win;

	public void show(AccountTransaction tx) {

		TxDetailsPanel panel = new TxDetailsPanel();
		panel.setMaximumSize(new Dimension(400, 400));

		panel.from.setText(tx.getFrom());
		panel.to.setText(tx.getTo());
		panel.amount.setText(tx.getAmount().toPlainString());
		panel.fee.setText(tx.getFee().toPlainString());
		panel.memo.setText(tx.getMemo());
		panel.uuid.setText(tx.getUuid());
		panel.date.setText(DateFormatUtils.formatUTC(tx.getDate(), DateFormatUtils.SMTP_DATETIME_FORMAT.getPattern()));

		new QTextComponentContextMenu(panel.from);
		new QTextComponentContextMenu(panel.to);
		new QTextComponentContextMenu(panel.amount);
		new QTextComponentContextMenu(panel.fee);
		new QTextComponentContextMenu(panel.memo);
		new QTextComponentContextMenu(panel.date);
		new QTextComponentContextMenu(panel.uuid);
		
		JDialog dialog = SwingHelper.dialog(win.getFrame(), panel);
		dialog.setResizable(false);

		SwingHelper.installEscapeCloseOperation(dialog);

		panel.btnClose.addActionListener(e -> {
			dialog.dispose();
		});

		dialog.setVisible(true);
	}

}
