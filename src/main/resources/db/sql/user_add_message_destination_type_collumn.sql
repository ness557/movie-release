alter table uzer
	add message_dest_type varchar(30);

update uzer set message_dest_type = 'EMAIL' where is_telegram_notify = false;
update uzer set message_dest_type = 'TELEGRAM' where is_telegram_notify = true;