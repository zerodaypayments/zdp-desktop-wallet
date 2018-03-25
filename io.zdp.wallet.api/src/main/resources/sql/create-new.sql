create table wallet 
	(
		id int primary key not null,
		uuid varchar(100) not null,
		date_created timestamp not null,
		date_update timestamp not null,
		version int not null,
		private_key varchar(100) not null,
		public_key varchar(100) not null,
		balance decimal(19,8) not null
	);
	
	