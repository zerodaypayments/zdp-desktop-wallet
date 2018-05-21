package io.zdp.wallet.api.db.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import io.zdp.wallet.api.db.domain.Account;

public interface AccountDao extends JpaRepository<Account, Long> {

	Account findByPrivateKey(String privateKey);

}
