package io.zdp.wallet.api.db.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import io.zdp.wallet.api.db.domain.Account;
import io.zdp.wallet.api.db.domain.Wallet;

public interface WalletDao extends JpaRepository<Wallet, Integer> {


}
