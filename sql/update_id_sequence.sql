select setval('cs_platform_id_seq', select max(id) from cs_platform);
select setval('cs_record_id_seq', select max(id) from cs_record);
select setval('cs_tag_id_seq', select max(id) from cs_tag);
select setval('pay_account_id_seq', select max(id) from pay_account;
select setval('pay_platform_id_seq', select max(id) from pay_platform));
