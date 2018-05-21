package io.zdp.wallet.api.db.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.zdp.crypto.Curves;
import io.zdp.crypto.key.ZDPKeyPair;
import io.zdp.wallet.api.db.dao.AccountDao;
import io.zdp.wallet.api.db.dao.WalletDao;
import io.zdp.wallet.api.db.domain.Account;
import io.zdp.wallet.api.db.domain.Wallet;
import io.zdp.wallet.api.db.service.WalletService;

@Service
public class WalletServiceImpl implements WalletService {

	@Autowired
	private WalletDao walletDao;

	@Autowired
	private AccountDao accountDao;

	@Override
	@Transactional(readOnly = true)
	public Wallet load() {
		if (walletDao.count() == 0) {
			return null;
		} else {
			return walletDao.findAll().iterator().next();
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void create(Wallet w) {
		walletDao.save(w);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveAccount(Account a) {
		accountDao.save(a);
	}

	@Override
	@Transactional(readOnly = true)
	public Account getAccount(String privateKey) {
		return accountDao.findByPrivateKey(privateKey);
	}

	@Override
	@Transactional(readOnly = false)
	public Account addAccount(Wallet w, String privateKey, String curve) {

		ZDPKeyPair kp = ZDPKeyPair.createFromPrivateKeyBase58(privateKey, curve);

		Account a = new Account();
		a.setWallet(w);
		a.setBalance(BigDecimal.ZERO);
		a.setCurve(Curves.getCurveIndex(curve));
		a.setHeight(0);
		a.setPrivateKey(privateKey);
		a.setPublicKey(kp.getPublicKeyAsBase58());
		a.setUuid(kp.getZDPAccount().getPublicKeyHash());
		a.setZdpUuid(kp.getZDPAccount().getUuid());
		
		w.getAccounts().add(a);

		return a;

	}

}
