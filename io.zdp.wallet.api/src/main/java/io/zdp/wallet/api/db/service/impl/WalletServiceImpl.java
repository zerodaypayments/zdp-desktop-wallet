package io.zdp.wallet.api.db.service.impl;

import java.math.BigDecimal;
import java.util.Optional;

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
	@Transactional ( readOnly = true )
	public Wallet load ( ) {
		if ( walletDao.count() == 0 ) {
			return null;
		} else {
			return walletDao.findAll().iterator().next();
		}
	}

	@Override
	@Transactional ( readOnly = false )
	public void create ( Wallet w ) {
		walletDao.save( w );
	}

	@Override
	@Transactional ( readOnly = false )
	public void saveAccount ( Account a ) {
		accountDao.save( a );
	}

	@Override
	@Transactional ( readOnly = true )
	public Account getAccount ( String privateKey ) {
		return accountDao.findByPrivateKey( privateKey );
	}

	@Override
	@Transactional ( readOnly = false )
	public Account addAccount ( Wallet wa, String privateKey, String curve ) {

		Optional < Wallet > wallet = this.walletDao.findById( wa.getId() );

		ZDPKeyPair kp = ZDPKeyPair.createFromPrivateKeyBase58( privateKey, curve );

		Account a = new Account();
		a.setWallet( wallet.get() );
		a.setBalance( BigDecimal.ZERO );
		a.setCurve( Curves.getCurveIndex( curve ) );
		a.setHeight( 0 );
		a.setPrivateKey( privateKey );
		a.setTransferChainHash( new byte [ ] {} );
		a.setPublicKey( kp.getPublicKeyAsBase58() );
		a.setUuid( kp.getZDPAccount().getPublicKeyHash() );
		a.setZdpUuid( kp.getZDPAccount().getUuid() );

		this.accountDao.save( a );

		wa.getAccounts().add( a );

		wallet.get().getAccounts().add( a );
		this.walletDao.save( wallet.get() );

		return a;

	}

	@Override
	@Transactional ( readOnly = false )
	public void updateAccountDetails ( Account account, BigDecimal balance, long height, byte [ ] chainHash ) {

		Optional < Account > acc = this.accountDao.findById( account.getId() );

		acc.get().setBalance( balance );
		acc.get().setHeight( height );
		acc.get().setTransferChainHash( chainHash );

		this.accountDao.save( acc.get() );

		account.setBalance( balance );
		account.setHeight( height );
		account.setTransferChainHash( chainHash );

	}

}
