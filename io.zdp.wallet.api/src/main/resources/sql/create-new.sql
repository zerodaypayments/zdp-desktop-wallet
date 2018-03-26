create table wallet 
	(
		id int primary key not null,
		uuid varchar(100) not null,
		version int not null,
		private_key varchar(100) not null,
		public_key varchar(100) not null,
		balance decimal(19,8) not null
	);
	
create table tx 
	(
		id integer primary key AUTOINCREMENT,
		uuid varchar(100) not null UNIQUE,
		amount decimal(19,8) not null,
		fee decimal(19,8),
		date DATETIME not null,
		from_address varchar(255) not null,
		to_address varchar(255) not null,
		memo varchar(64) 
	);
