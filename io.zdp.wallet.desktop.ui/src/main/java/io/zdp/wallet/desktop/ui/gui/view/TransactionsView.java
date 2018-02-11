package io.zdp.wallet.desktop.ui.gui.view;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.zdp.wallet.desktop.ui.common.SwingHelper;
import io.zdp.wallet.desktop.ui.gui.dialog.HomePanelWithEmptyAddressBook;
import io.zdp.wallet.desktop.ui.service.DesktopWalletService;

@Component
public class TransactionsView {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DesktopWalletService walletService;

	public JPanel get() {

		JPanel panel = new JPanel(new BorderLayout());
		
		panel.add(new JTree());


		return panel;

	}

}
