select setval('cs_platform_id_seq', (select max(id) from cs_platform) + 1);
select setval('cs_record_id_seq', (select max(id) from cs_record) + 1);
select setval('cs_tag_id_seq', (select max(id) from cs_tag) + 1);
select setval('pay_account_id_seq', (select max(id) from pay_account) + 1);
select setval('pay_platform_id_seq', (select max(id) from pay_platform) + 1);
